package com.zappos.runner;/*
 *  CONFIDENTIAL AND PROPRIETARY INFORMATION. Copyright Sunrun, Inc. 2013.  All Rights Reserved. This file may not be
 *
 */

import com.zappos.business.ZapposClient;

public class CommandLineRunner {
    public static void main(final String[] args) throws Exception {
        final ZapposClient searchResource = new ZapposClient();
        searchResource.search("boots");
    }
}

