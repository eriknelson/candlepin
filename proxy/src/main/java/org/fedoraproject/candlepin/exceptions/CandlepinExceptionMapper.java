/**
 * Copyright (c) 2009 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.fedoraproject.candlepin.exceptions;

import static org.jboss.resteasy.util.MediaTypeHelper.getBestMatch;
import static org.jboss.resteasy.util.MediaTypeHelper.parseHeader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.xnap.commons.i18n.I18n;

import com.google.inject.Inject;
import com.google.inject.Injector;


/**
 * BadRequestExceptionMapper
 */
@Provider
public class CandlepinExceptionMapper implements
    ExceptionMapper<RuntimeException> {
    private Logger logger = Logger.getLogger(CandlepinExceptionMapper.class);
    private static final List<MediaType> DESIRED_RESPONSE_TYPES = 
        new LinkedList<MediaType>() {
            {
                add(MediaType.APPLICATION_JSON_TYPE);
                add(MediaType.TEXT_PLAIN_TYPE);
                add(MediaType.APPLICATION_ATOM_XML_TYPE);
            }
        };

    @Inject
    private Injector injector;

    private I18n i18n;

    @Override
    public Response toResponse(RuntimeException exception) {
        logger.error("Runtime exception:", exception);
        HttpServletRequest request = injector
            .getInstance(HttpServletRequest.class);

        i18n = injector.getInstance(I18n.class);

        String header = request.getHeader(HttpHeaderNames.ACCEPT);
        MediaType responseMediaType = null;
        if (header != null) {
            List<MediaType> headerMediaTypes = parseHeader(header);

            responseMediaType = headerMediaTypes.size() == 0 ? MediaType.TEXT_PLAIN_TYPE :
                getBestMatch(DESIRED_RESPONSE_TYPES, headerMediaTypes);
        }
        if (responseMediaType == null || (responseMediaType.getType().equals("*") && 
                responseMediaType.getSubtype().equals("*"))) {
            responseMediaType = MediaType.APPLICATION_JSON_TYPE;
        }

        ResponseBuilder bldr = null;
        // Resteasy wraps the actual exception sometimes
        Throwable cause = exception.getCause() == null ? exception : exception.getCause();
        if (cause instanceof CandlepinException || 
                exception instanceof CandlepinException) {
            bldr = getBuilder((CandlepinException) cause, responseMediaType);
        }
        else if (cause instanceof DefaultOptionsMethodException) {
            Response resp = ((Failure) cause).getResponse();
            return (resp != null) ? resp : 
                getDefaultBuilder(cause, responseMediaType).build();
        }
        else if (cause instanceof org.jboss.resteasy.spi.BadRequestException ||
            exception instanceof org.jboss.resteasy.spi.BadRequestException) {
            bldr = Response.status(Status.BAD_REQUEST).type(
                responseMediaType);
            String msg = exception.getMessage();
            if (StringUtils.isNotEmpty(msg)) {
                bldr.entity(new ExceptionMessage(extractIllegalValue(msg)));
            }
        }
        else {
            bldr = getDefaultBuilder(cause, responseMediaType);
        }

        return bldr.build();
    }

    /**
     * @param msg
     * @return
     */
    private String extractIllegalValue(String msg) {
        Matcher paramMatcher = PARAM_REGEX.matcher(msg);
        Matcher illegalValMatcher = ILLEGAL_VAL_REGEX.matcher(msg);
        if (paramMatcher.find() && illegalValMatcher.find()) {
            if ((paramMatcher.groupCount() & illegalValMatcher.groupCount()) == 2) {
                return i18n.tr("{0} is not a valid value for {1}",
                    illegalValMatcher.group(1), paramMatcher.group(1));
            }
        }
        return i18n.tr("Bad Request");
    }

    private ResponseBuilder getBuilder(CandlepinException exception,
        MediaType responseMediaType) {
        ResponseBuilder bldr = Response.status(exception.httpReturnCode())
            .entity(exception.message()).type(responseMediaType);

        for (Map.Entry<String, String> hdr : exception.headers().entrySet()) {
            bldr.header(hdr.getKey(), hdr.getValue());
        }

        return bldr;
    }
    private static final Pattern PARAM_REGEX = Pattern
            .compile("(?:javax\\.ws\\.rs\\.\\w+\\(\\\")([\\w\\s]+)(\\\"\\))");
    private static final Pattern ILLEGAL_VAL_REGEX =
            Pattern.compile(":?value\\sis\\s'([\\w\\s]+)(:?'\\sfor)");
    
    private ResponseBuilder getDefaultBuilder(Throwable exception,
        MediaType responseMediaType) {
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        StackTraceElement[] stes = cause.getStackTrace();
        StackTraceElement ele = stes[0];
        int line = ele.getLineNumber();
        String method = ele.getMethodName();
        String clazz = ele.getClassName();
        String message = i18n.tr("Runtime Error {0} at {1}.{2}:{3}", exception
            .getMessage(), clazz, method, line);
        ResponseBuilder bldr = Response.status(Status.INTERNAL_SERVER_ERROR)
            .entity(new ExceptionMessage(message)).type(responseMediaType);
        return bldr;
    }
    
}
