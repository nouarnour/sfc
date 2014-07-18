/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.sfc.provider;

import org.opendaylight.controller.md.sal.common.api.data.DataChangeEvent;
import org.opendaylight.controller.sal.binding.api.data.DataChangeListener;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.ServiceFunctionForwarders;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;

/**
 * This class is the DataListener for SFF changes.
 *
 * <p>
 * @author Reinaldo Penno (rapenno@gmail.com)
 * @version 0.1
 * @since       2014-06-30
 */
public class SfcProviderSffDataListener implements DataChangeListener  {

    private static final Logger LOG = LoggerFactory.getLogger(SfcProviderSnDataListener.class);
    private static final OpendaylightSfc odlSfc = OpendaylightSfc.getOpendaylightSfcObj();

    @Override
    public void onDataChanged(
            DataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        LOG.debug("\n########## Start: {}", Thread.currentThread().getStackTrace()[1]);


        /*
         * when a SFF is created we will process and send it to southbound devices. But first we need
         * to mae sure all info is present or we will pass.
         */
        boolean sffready = false;
        Map<InstanceIdentifier<?>, DataObject> dataUpdatedConfigurationObject = change.getUpdatedConfigurationData();

        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : dataUpdatedConfigurationObject.entrySet())
        {
            if( entry.getValue() instanceof ServiceFunctionForwarders) {

                ServiceFunctionForwarders updatedServiceFunctionForwarders = (ServiceFunctionForwarders) entry.getValue();
                List<ServiceFunctionForwarder> serviceFunctionForwarderList = updatedServiceFunctionForwarders.getServiceFunctionForwarder();
                for (ServiceFunctionForwarder serviceFunctionForwarder : serviceFunctionForwarderList) {
                    sffready = SfcProviderServiceForwarderAPI.checkServiceFunctionForwarder(serviceFunctionForwarder);
                    sffready &= sffready;

                }
                if (sffready) {
                    odlSfc.executor.execute(SfcProviderRestAPI.getSfcProviderRestAPIPut(updatedServiceFunctionForwarders));
                    //send to REST
                }
            }
        }


        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
    }

}