/*
 * Copyright (c) 2016, 2017 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.sfc.renderers.iosxe.listeners;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.serviceutils.tools.mdsal.listener.AbstractSyncDataTreeChangeListener;
import org.opendaylight.sfc.renderers.iosxe.IosXeServiceForwarderMapper;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.ServiceFunctionForwarders;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

@Singleton
public class ServiceForwarderListener extends AbstractSyncDataTreeChangeListener<ServiceFunctionForwarders> {

    private final IosXeServiceForwarderMapper sffManager;

    @Inject
    public ServiceForwarderListener(DataBroker dataBroker, IosXeServiceForwarderMapper sffManager) {
        super(dataBroker, LogicalDatastoreType.CONFIGURATION,
              InstanceIdentifier.builder(ServiceFunctionForwarders.class).build());
        this.sffManager = sffManager;
    }

    @Override
    public void add(@NonNull InstanceIdentifier<ServiceFunctionForwarders> instanceIdentifier,
                    @NonNull ServiceFunctionForwarders serviceFunctionForwarders) {
        update(instanceIdentifier, serviceFunctionForwarders, serviceFunctionForwarders);
    }

    @Override
    public void remove(@NonNull InstanceIdentifier<ServiceFunctionForwarders> instanceIdentifier,
                       @NonNull ServiceFunctionForwarders serviceFunctionForwarders) {
        if (serviceFunctionForwarders.getServiceFunctionForwarder() != null) {
            sffManager.syncForwarders(serviceFunctionForwarders.getServiceFunctionForwarder(), true);
        }
    }

    @Override
    public void update(@NonNull InstanceIdentifier<ServiceFunctionForwarders> instanceIdentifier,
                       @NonNull ServiceFunctionForwarders originalServiceFunctionForwarders,
                       @NonNull ServiceFunctionForwarders updatedServiceFunctionForwarders) {
        if (updatedServiceFunctionForwarders.getServiceFunctionForwarder() != null) {
            sffManager.syncForwarders(updatedServiceFunctionForwarders.getServiceFunctionForwarder(), false);
        }
    }
}
