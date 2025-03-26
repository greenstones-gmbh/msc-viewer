package de.greenstones.gsmr.msc.simulator;

import de.greenstones.gsmr.msc.clients.test.TestData;
import de.greenstones.gsmr.msc.clients.test.TestOutputGenerator;
import de.greenstones.gsmr.msc.clients.test.TestOutputService;

public class TestSimulator {
    public static void main(String[] args) {
        TestOutputService outputService = new TestOutputService(new TestOutputGenerator("MSS-DEV-01"),
                TestData.createMany());
        String output = outputService.getOutput("ZEPO:NO=10000");
        System.err.println(output);
    }
}
