/**
 * Copyright (c) 2009 - 2012 Red Hat, Inc.
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
package org.candlepin.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

/**
 * Represents an ContentDeliveryNetwork within an owner/organization. ContentDeliveryNetworks are tracked
 * primarily so we can enable/disable/promote content in specific places.
 *
 * Not all deployments of Candlepin will make use of this table, it will at times
 * be completely empty.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@Entity
@Table(name = "cp_cdn",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"key"})})
public class ContentDeliveryNetwork extends AbstractHibernateObject {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String id;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "certificate_id")
    private ContentDeliveryNetworkCertificate cert;

    public ContentDeliveryNetwork() {
    }

    public ContentDeliveryNetwork(String key, String name, String url,
        ContentDeliveryNetworkCertificate cert) {
        this.key = key;
        this.name = name;
        this.url = name;
        this.cert = cert;
    }

    /**
     * @param string
     * @param string2
     * @param string3
     */
    public ContentDeliveryNetwork(String key, String name, String url) {
        this.key = key;
        this.name = name;
        this.url = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ContentDeliveryNetworkCertificate getCertificate() {
        return cert;
    }

    public void setCertificate(ContentDeliveryNetworkCertificate cert) {
        this.cert = cert;
    }


}
