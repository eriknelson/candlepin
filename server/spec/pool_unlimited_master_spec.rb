require 'spec_helper'
require 'candlepin_scenarios'

describe 'Unlimited Master Pools' do
  include CandlepinMethods
  include SpecUtils
  include CertificateMethods

  before(:each) do
    @owner = create_owner random_string('owner')
    @user = user_client(@owner, random_string('user'))

    @uuid = random_string('system.uuid')
    @physical_sys = @user.register(random_string('host'), :system)
    @physical_client = Candlepin.new(nil, nil, @physical_sys['idCert']['cert'], @physical_sys['idCert']['key'])
    @physical_client.update_consumer({:guestIds => [{'guestId' => @uuid}]})

    @guest = @user.register(random_string('guest'), :system, nil, {'virt.uuid' => @uuid, 'virt.is_guest' => 'true'})
    @guest_client = Candlepin.new(nil, nil, @guest['idCert']['cert'], @guest['idCert']['key'])

    @guest_unmapped = @user.register(random_string('guest'), :system, nil, {'virt.is_guest' => 'true'})
    @guest_client_unmapped = Candlepin.new(nil, nil, @guest_unmapped['idCert']['cert'], @guest_unmapped['idCert']['key'])

    @product = create_product(nil, nil, {
      :attributes => {
        :virt_limit => "unlimited",
        'multi-entitlement' => "yes"
      }
    })

    @product_no_virt = create_product(nil, nil, {
      :attributes => {
        'multi-entitlement' => "yes"
      }
    })

    @pool = create_pool_and_subscription(@owner['key'], @product.id, -1, [], '', '', '', nil, nil, false)
    @pool_no_virt = create_pool_and_subscription(@owner['key'], @product_no_virt.id, -1, [], '', '', '', nil, nil, false)
    @pools = @cp.list_pools :owner => @owner.id, :product => @product.id
    @pools.size.should == 2
    @pools = @cp.list_pools :owner => @owner.id, :product => @product_no_virt.id
    @pools.size.should == 1
  end

  it 'allows system to consume unlimited quantity pool' do
    @physical_client.consume_pool(@pool['id'], {:quantity => 300})
    ents = @physical_client.list_entitlements
    ents.size.should == 1
    ents[0].quantity.should == 300
  end

  it 'allows system to consume unlimited no-virt quantity pool' do
    @physical_client.consume_pool(@pool_no_virt['id'], {:quantity => 3200})
    ents = @physical_client.list_entitlements
    ents.size.should == 1
    ents[0].quantity.should == 3200
  end

 it 'allows mapped guest to consume unlimited quantity pool' do
    skip("candlepin running in hosted mode") if is_hosted?
    @physical_client.consume_pool(@pool['id'], {:quantity => 300})
    @pools = @cp.list_pools :owner => @owner.id, :product => @product.id
    guest_pool = nil
    @pools.each do |pool|
        if flatten_attributes(pool['attributes'])["pool_derived"] == "true" and pool.type == 'ENTITLEMENT_DERIVED'
           guest_pool = pool
        end
    end
    @guest_client.consume_pool(guest_pool['id'], {:uuid => @guest.uuid, :quantity => 5000})
    ents = @guest_client.list_entitlements
    ents.size.should == 1
    ents[0].quantity.should == 5000
  end

 it 'allows unmapped guest to consume unlimited quantity pool' do
    skip("candlepin running in hosted mode") if is_hosted?
    @pools = @cp.list_pools :owner => @owner.id, :product => @product.id
    guest_pool = nil
    @pools.each do |pool|
        if flatten_attributes(pool['attributes'])["pool_derived"] == "true" and pool.type == 'UNMAPPED_GUEST'
           guest_pool = pool
        end
    end
    guest_pool.should_not be nil
    @guest_client_unmapped.consume_pool(guest_pool['id'], {:uuid => @guest_unmapped.uuid, :quantity => 4000})
    ents = @guest_client_unmapped.list_entitlements
    ents.size.should == 1
    ents[0].quantity.should == 4000
  end

end
