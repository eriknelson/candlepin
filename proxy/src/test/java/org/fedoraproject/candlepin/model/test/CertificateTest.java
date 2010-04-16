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



package org.fedoraproject.candlepin.model.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.fedoraproject.candlepin.model.SubscriptionsCertificate;
import org.fedoraproject.candlepin.model.Owner;
import org.fedoraproject.candlepin.test.DatabaseTestFixture;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class CertificateTest extends DatabaseTestFixture {

    @Before
    public void setUpTestObjects() {
        
        String ownerName = "Example Corporation";
        Owner owner = new Owner(ownerName);
        
        ownerCurator.create(owner);
        certificateCurator.create(
                new SubscriptionsCertificate(
                "This is not actually a certificate. No entitlements for you!",
                owner));
    }
    
    
    @Ignore
    public void testGetCertificate() {
        //Certificate newCertificate = 
        new SubscriptionsCertificate();
    }
    
    @Test
    public void testList() throws Exception {
        List<SubscriptionsCertificate> certificates = certificateCurator.findAll(); 
        int beforeCount = certificates.size();
     
        for (int i = 0; i < 10; i++) {
            Owner owner = new Owner("owner" + i);
            ownerCurator.create(owner);
            certificateCurator.create(new SubscriptionsCertificate(
                "this is a test", owner));
        }
        
        certificates =  certificateCurator.findAll();
        int afterCount = certificates.size();
        assertEquals(10, afterCount - beforeCount);
    }
    
    @Test
    public void testLookup() throws Exception {
        
        Owner owner = new Owner("test company");
        SubscriptionsCertificate certificate = new SubscriptionsCertificate(
            "not a cert", owner);
        
        ownerCurator.create(owner);
        certificateCurator.create(certificate);
        
        SubscriptionsCertificate lookedUp = certificateCurator.find(certificate.getId()); 

        assertEquals(certificate.getId(), lookedUp.getId());
        assertEquals(certificate.getCertificate(), lookedUp.getCertificate());
    }
}

