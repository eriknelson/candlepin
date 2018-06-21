/**
 * Copyright (c) 2009 - 2018 Red Hat, Inc.
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
package org.candlepin.dto.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang.builder.HashCodeBuilder;



/**
 * The ActivationKeyContentOverrideDTO is used as a DTO for the ActivationKeyContentOverride
 * instances.
 */
public class ActivationKeyContentOverrideDTO extends ContentOverrideDTO<ActivationKeyContentOverrideDTO> {
    public static final long serialVersionUID = 1L;

    protected ActivationKeyDTO activationKey;


    /**
     * Initializes a new ActivationKeyContentOverrideDTO instance with null values.
     */
    public ActivationKeyContentOverrideDTO() {
        // Intentionally left empty
    }

    /**
     * Initializes a new ActivationKeyContentOverrideDTO instance which is a shallow copy of the
     * provided source entity.
     *
     * @param source
     *  The source entity to copy
     */
    public ActivationKeyContentOverrideDTO(ActivationKeyContentOverrideDTO source) {
        super(source);
    }

    /**
     * Fetches the activation key that's overriding the content. If the activation key has not been
     * set, this method returns null.
     *
     * @return
     *  The activation key overriding the content, or null if the activation key has not been set
     */
    @JsonIgnore
    public ActivationKeyDTO getActivationKey() {
        return this.activationKey;
    }

    /**
     * Sets or clears the activation key that's overriding the content. If the provided key is null,
     * any key currently set will be cleared.
     *
     * @param activationKey
     *  The activation key that's overriding the content, or null to clear the key
     *
     * @return
     *  A reference to this DTO
     */
    @JsonProperty("key")
    public ActivationKeyContentOverrideDTO setActivationKey(ActivationKeyDTO activationKey) {
        this.activationKey = activationKey;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ActivationKeyDTO key = this.getActivationKey();

        return String.format("ActivationKeyContentOverrideDTO [key: %s, content: %s, name: %s, value: %s]",
            (key != null ? key.getId() : null),
            this.getContentLabel(),
            this.getName(),
            this.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ActivationKeyContentOverrideDTO && super.equals(obj)) {
            ActivationKeyContentOverrideDTO that = (ActivationKeyContentOverrideDTO) obj;

            ActivationKeyDTO thisKey = this.getActivationKey();
            ActivationKeyDTO thatKey = that.getActivationKey();

            String thisKeyId = thisKey != null ? thisKey.getId() : null;
            String thatKeyId = thatKey != null ? thatKey.getId() : null;

            return thisKeyId != null ? thisKeyId.equals(thatKeyId) : thatKeyId == null;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        ActivationKeyDTO key = this.getActivationKey();

        HashCodeBuilder builder = new HashCodeBuilder(37, 7)
            .append(super.hashCode())
            .append(key != null ? key.getId() : null);

        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivationKeyContentOverrideDTO clone() {
        ActivationKeyContentOverrideDTO copy = super.clone();

        ActivationKeyDTO key = this.getActivationKey();
        copy.setActivationKey(key != null ? key.clone() : null);

        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivationKeyContentOverrideDTO populate(ActivationKeyContentOverrideDTO source) {
        super.populate(source);

        this.setActivationKey(source.getActivationKey());

        return this;
    }

}
