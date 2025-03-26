package de.greenstones.gsmr.msc.clients.test;

import java.util.Map;

import de.greenstones.gsmr.msc.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class TestOutputService {

    TestOutputGenerator generator;
    TestData data;

    public String getOutput(String cmd) {
        if (cmd.matches("^ZELO$")) {
            return generator.generate("ZELO", Map.of(
                    "lacs", data.lacs));
        }
        if (cmd.matches("^ZELO:LAC=\\d+,MCC=\\d+,MNC=\\d+$")) {
            String id = cmd.replace("ZELO:", "");
            return generator.generate("ZELO-detail", data.findLac(id));
        }

        if (cmd.matches("^ZEPO::IDE$")) {
            return generator.generate("ZEPO::IDE", Map.of(
                    "cells", data.btss));
        }

        if (cmd.matches("^ZEPO:NO=\\d+$")) {
            String id = cmd.replace("ZEPO:", "");
            return generator.generate("ZEPO-detail", data.findBts(id));
        }

        if (cmd.matches("^ZHCO$")) {
            return generator.generate("ZHCO", Map.of(
                    "celllists", data.cellLists));
        }

        if (cmd.matches("^ZHCO:CLNAME=.+$")) {
            String id = cmd.replace("ZHCO:", "");
            return generator.generate("ZHCO-detail", data.findCellList(id));
        }

        if (cmd.matches("^ZHAO$")) {
            return generator.generate("ZHAO", Map.of(
                    "gcas", data.gcas));
        }

        if (cmd.matches("^ZHAO:GCAC=\\d+$")) {
            String id = cmd.replace("ZHAO:", "");
            return generator.generate("ZHAO-detail", data.findGca(id));
        }

        if (cmd.matches("^ZHGO$")) {
            return generator.generate("ZHGO", Map.of(
                    "gcrefs", data.gcrefs));
        }

        if (cmd.matches("^ZHGO:GCREF=\\d+:::STYPE=.*$")) {
            String id = cmd.replace("ZHGO:", "");
            return generator.generate("ZHGO-detail", data.findGcref(id));
        }

        if (cmd.matches("^ZEPJ:ALL$")) {
            return generator.generate("ZEPJ:ALL", Map.of(
                    "ltes", data.ltes));
        }

        if (cmd.matches("^ZEPJ:ECGI:ECI=\\d+,EMCC=\\d+,EMNC=\\d+$")) {
            String id = cmd.replace("ZEPJ:", "");
            return generator.generate("ZEPJ-detail", data.findLte(id));
        }

        throw new ApplicationException("Command not supported: " + cmd);
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestOutputService service = new TestOutputService(new TestOutputGenerator("MSS-DEMO"), TestData.create());
        System.out.println(service.getOutput("ZELO"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZELO:LAC=00100,MCC=999,MNC=06"));

        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZEPO::IDE"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZEPO:NO=10000"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZHCO"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZHCO:CLNAME=CLIST10000"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZHAO"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZHAO:GCAC=88888"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZHGO"));
        System.out.println(service.getOutput("ZHAO"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZHGO:GCREF=88888666:::STYPE=VGCS"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZEPJ:ALL"));
        System.err.println("-------------------------------------------------");
        System.out.println(service.getOutput("ZEPJ:ECGI:ECI=22222,EMCC=999,EMNC=04"));
        System.out.println(service.getOutput("ZHCO:CLNAME=CLIST10000"));

        // MscParser parser = new MscParser();
        // System.out.println(parser.obj(service.getOutput("ZEPO:NO=10000"), "BASE
        // TRANSCEIVER STATION"));
    }

}