/**
 * Copyright (c) 2009 - 2017 Red Hat, Inc.
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

import static org.junit.Assert.*;

import junitparams.JUnitParamsRunner;
import org.candlepin.dto.AbstractTranslatorTest;
import org.candlepin.dto.ModelTranslator;
import org.candlepin.model.Pool;
import org.candlepin.model.Product;
import org.candlepin.model.Release;
import org.candlepin.model.activationkeys.ActivationKey;
import org.candlepin.model.activationkeys.ActivationKeyContentOverride;
import org.candlepin.model.activationkeys.ActivationKeyPool;
import org.junit.runner.RunWith;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;



/**
 * Test suite for the ActivationKeyTranslator class
 */
@RunWith(JUnitParamsRunner.class)
public class ActivationKeyTranslatorTest extends
    AbstractTranslatorTest<ActivationKey, ActivationKeyDTO, ActivationKeyTranslator> {

    protected ActivationKeyTranslator translator = new ActivationKeyTranslator();

    protected OwnerTranslatorTest ownerTranslatorTest = new OwnerTranslatorTest();

    @Override
    protected void initModelTranslator(ModelTranslator modelTranslator) {
        this.ownerTranslatorTest.initModelTranslator(modelTranslator);
        modelTranslator.registerTranslator(this.translator, ActivationKey.class, ActivationKeyDTO.class);
    }

    @Override
    protected ActivationKeyTranslator initObjectTranslator() {
        return this.translator;
    }

    @Override
    protected ActivationKey initSourceObject() {
        ActivationKey source = new ActivationKey();
        source.setId("key-id");
        source.setName("key-name");
        source.setDescription("key-description");
        source.setOwner(this.ownerTranslatorTest.initSourceObject());
        source.setReleaseVer(new Release("key-release-ver"));
        source.setServiceLevel("key-service-level");
        source.setAutoAttach(true);


        Set<Product> products = new HashSet<>();
        Set<ActivationKeyPool> akpools = new HashSet<>();

        for (int i = 0; i < 3; ++i) {
            Product product = new Product();
            product.setId("test_prod-" + i);

            Pool pool = new Pool();
            pool.setId("test_pool-" + i);

            ActivationKeyPool akp = new ActivationKeyPool(source, pool, 1L);

            products.add(product);
            akpools.add(akp);
        }

        source.setProducts(products);
        source.setPools(akpools);

        return source;
    }

    @Override
    protected ActivationKeyDTO initDestinationObject() {
        return new ActivationKeyDTO();
    }

    @Override
    protected void verifyOutput(ActivationKey source, ActivationKeyDTO dest, boolean childrenGenerated) {
        if (source != null) {
            assertEquals(source.getId(), dest.getId());
            assertEquals(source.getName(), dest.getName());
            assertEquals(source.getDescription(), dest.getDescription());
            assertEquals(source.getServiceLevel(), dest.getServiceLevel());
            assertEquals(source.isAutoAttach(), dest.isAutoAttach());

            // Check product IDs
            Collection<Product> products = source.getProducts();
            Collection<String> productIds = dest.getProductIds();

            if (products != null) {
                assertNotNull(productIds);
                assertEquals(products.size(), productIds.size());

                for (Product product : products) {
                    assertNotNull(product);
                    assertNotNull(product.getId());

                    assertTrue(productIds.contains(product.getId()));
                }
            }
            else {
                assertNull(productIds);
            }

            // Check content overrides
            Collection<ActivationKeyContentOverride> overrides = source.getContentOverrides();
            Collection<ActivationKeyContentOverrideDTO> overrideDTOs = dest.getContentOverrides();


            if (overrides != null) {
                int matches = 0;
                assertNotNull(overrideDTOs);
                assertEquals(overrides.size(), overrideDTOs.size());

                for (ActivationKeyContentOverride override : overrides) {
                    assertNotNull(override);

                    for (ActivationKeyContentOverrideDTO odto : overrideDTOs) {
                        assertNotNull(odto);

                        assertSame(dest, odto.getActivationKey());

                        EqualsBuilder builder = new EqualsBuilder()
                            .append(override.getContentLabel(), odto.getContentLabel())
                            .append(override.getName(), odto.getName());

                        if (builder.isEquals()) {
                            assertEquals(override.getValue(), odto.getValue());
                            ++matches;
                        }
                    }
                }

                assertEquals(overrides.size(), matches);
            }
            else {
                assertNull(overrideDTOs);
            }

            // Check release version
            Release releaseSource = source.getReleaseVer();
            String releaseDestination = dest.getReleaseVersion();

            if (releaseSource != null) {
                assertEquals(releaseSource.getReleaseVer(), releaseDestination);
            }
            else {
                assertNull(releaseDestination);
            }

            // Check nested DTOs
            if (childrenGenerated) {
                this.ownerTranslatorTest.verifyOutput(source.getOwner(), dest.getOwner(), true);

                for (ActivationKeyPool akPool : source.getPools()) {
                    for (ActivationKeyDTO.ActivationKeyPoolDTO akPoolDto : dest.getPools()) {

                        assertNotNull(akPoolDto);

                        if (akPoolDto.getPoolId().equals(akPool.getId())) {
                            assertEquals(akPool.getQuantity(), akPoolDto.getQuantity());
                        }
                    }
                }
            }
            else {
                assertNull(dest.getOwner());
                assertNull(dest.getPools());
            }
        }
        else {
            assertNull(dest);
        }
    }
}
