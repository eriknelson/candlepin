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
 * The ConsumerContentOverrideDTO is used as a DTO for the ConsumerContentOverride
 * instances.
 */
public class ConsumerContentOverrideDTO extends ContentOverrideDTO<ConsumerContentOverrideDTO> {
    public static final long serialVersionUID = 1L;

    protected ConsumerDTO consumer;


    /**
     * Initializes a new ConsumerContentOverrideDTO instance with null values.
     */
    public ConsumerContentOverrideDTO() {
        // Intentionally left empty
    }

    /**
     * Initializes a new ConsumerContentOverrideDTO instance which is a shallow copy of the provided
     * source entity.
     *
     * @param source
     *  The source entity to copy
     */
    public ConsumerContentOverrideDTO(ConsumerContentOverrideDTO source) {
        super(source);
    }

    /**
     * Fetches the consumer that's overriding the content. If the consumer has not been
     * set, this method returns null.
     *
     * @return
     *  The consumer overriding the content, or null if the consumer has not been set
     */
    @JsonIgnore
    public ConsumerDTO getConsumer() {
        return this.consumer;
    }

    /**
     * Sets or clears the consumer that's overriding the content. If the provided key is null,
     * any key currently set will be cleared.
     *
     * @param consumer
     *  The consumer that's overriding the content, or null to clear the key
     *
     * @return
     *  A reference to this DTO
     */
    @JsonProperty("consumer")
    public ConsumerContentOverrideDTO setConsumer(ConsumerDTO consumer) {
        this.consumer = consumer;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ConsumerDTO consumer = this.getConsumer();

        return String.format("ConsumerContentOverrideDTO [consumer: %s, content: %s, name: %s, value: %s]",
            (consumer != null ? consumer.getId() : null),
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

        if (obj instanceof ConsumerContentOverrideDTO && super.equals(obj)) {
            ConsumerContentOverrideDTO that = (ConsumerContentOverrideDTO) obj;

            ConsumerDTO thisConsumer = this.getConsumer();
            ConsumerDTO thatConsumer = that.getConsumer();

            String thisConsumerId = thisConsumer != null ? thisConsumer.getId() : null;
            String thatConsumerId = thatConsumer != null ? thatConsumer.getId() : null;

            return thisConsumerId != null ? thisConsumerId.equals(thatConsumerId) : thatConsumerId == null;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        ConsumerDTO consumer = this.getConsumer();

        HashCodeBuilder builder = new HashCodeBuilder(37, 7)
            .append(super.hashCode())
            .append(consumer != null ? consumer.getId() : null);

        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsumerContentOverrideDTO clone() {
        ConsumerContentOverrideDTO copy = super.clone();

        ConsumerDTO consumer = this.getConsumer();
        copy.setConsumer(consumer != null ? consumer.clone() : null);

        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsumerContentOverrideDTO populate(ConsumerContentOverrideDTO source) {
        super.populate(source);

        this.setConsumer(source.getConsumer());

        return this;
    }

}
