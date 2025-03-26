package de.greenstones.gsmr.msc.clients.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.greenstones.gsmr.msc.ApplicationException;
import de.greenstones.gsmr.msc.core.IdConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TestData {
    List<Bsc> bscs;
    List<Lac> lacs;
    List<Bts> btss;
    List<CellList> cellLists;
    List<Gca> gcas;
    List<Gcref> gcrefs;
    List<Lte> ltes;

    public Lac findLac(String id) {
        return lacs.stream().filter(lac -> {
            return lac.getMscId().equals(id);
        }).findFirst().orElseThrow(() -> new ApplicationException("LAC with id " + id + " not found"));
    }

    public Bts findBts(String id) {
        return btss.stream().filter(lac -> {
            String lid = String.format("NO=%s", lac.no);
            return lid.equals(id);
        }).findFirst().orElseThrow(() -> new ApplicationException("BTS with id " + id + " not found"));

    }

    public CellList findCellList(String id) {
        return cellLists.stream().filter(celllist -> {
            String lid = String.format("CLNAME=%s", celllist.name);
            return lid.equals(id);
        }).findFirst().orElseThrow(() -> new ApplicationException("CellList with id " + id + " not found"));

    }

    public Gca findGca(String id) {
        return gcas.stream().filter(gca -> {
            String lid = String.format("GCAC=%s", gca.code);
            return lid.equals(id);
        }).findFirst().orElseThrow(() -> new ApplicationException("GCA with id " + id + " not found"));

    }

    public Gcref findGcref(String id) {
        return gcrefs.stream().filter(gcref -> {
            String lid = String.format("GCREF=%s:::STYPE=%s", gcref.gcref, gcref.type);
            return lid.equals(id);
        }).findFirst().orElseThrow(() -> new ApplicationException("GCREF with id " + id + " not found"));

    }

    public Lte findLte(String id) {
        return ltes.stream().filter(lte -> {
            String lid = String.format("ECGI:ECI=%s,EMCC=%s,EMNC=%s", lte.eci, lte.emcc, lte.emnc);
            return lid.equals(id);
        }).findFirst().orElseThrow(() -> new ApplicationException("LTE with id " + id + " not found"));

    }

    public static TestData create() {
        TestData data = new TestData();
        data.bscs = Arrays.asList(
                new Bsc("BSC01", "1"), //
                new Bsc("BSC02", "2") //
        );

        data.lacs = Arrays.asList(//
                new Lac("LAC100", "00100", "999", "06"), //
                new Lac("LAC200", "00200", "999", "06"),
                new Lac("LAC300", "00300", "999", "06")//
        );

        data.btss = Arrays.asList(//
                Bts.create("10004", data.lacs.get(2), data.bscs.get(1)),
                Bts.create("10000", data.lacs.get(0), data.bscs.get(0)),
                Bts.create("10001", data.lacs.get(0), data.bscs.get(1)),
                Bts.create("10002", data.lacs.get(1), data.bscs.get(1)),
                Bts.create("10003", data.lacs.get(1), data.bscs.get(0)));

        data.cellLists = data.btss.stream().map(bts -> CellList.create(bts)).toList();

        data.gcas = Arrays.asList(//
                new Gca("99999", "GCAN99999", Arrays.asList(data.cellLists.get(0), data.cellLists.get(1))), //
                new Gca("88888", "GCAN88888", Arrays.asList(data.cellLists.get(2), data.cellLists.get(3))) //
        );

        data.gcrefs = Arrays.asList(//
                Gcref.create("555", "VGCS", data.gcas.get(0), Arrays.asList(data.cellLists.get(0))), //
                Gcref.create("333", "VGCS", data.gcas.get(0), Arrays.asList(data.cellLists.get(1))), //
                Gcref.create("666", "VGCS", data.gcas.get(1), Arrays.asList(data.cellLists.get(2))), //
                Gcref.create("777", "VGCS", data.gcas.get(1), Arrays.asList(data.cellLists.get(3))) //
        );

        data.ltes = Arrays.asList(//
                new Lte("11111", "999", "04", data.btss.get(0)), //
                new Lte("22222", "999", "04", data.btss.get(1)) //
        );
        return data;
    }

    public static TestData create(String name) {
        if (name != null && name.equals("big")) {
            return createMany();
        }
        return create();
    }

    public static TestData createMany() {

        TestData data = new TestData();
        data.bscs = Arrays.asList(
                new Bsc("BSC01", "1"), //
                new Bsc("BSC02", "2") //
        );

        data.lacs = repeat(15).map(i -> 100 * (i + 1))
                .map(i -> new Lac("LAC" + i, IdConverter.leftPad("" + i, 5, '0'), "999", "06")).toList();

        data.btss = repeat(250).map(i -> 10000 + i * 100)
                .map(i -> Bts.create("" + i, random(data.lacs),
                        random(data.bscs)))
                .toList();

        data.cellLists = data.btss.stream().map(bts -> CellList.create(bts)).toList();

        data.gcas = repeat(50).map(i -> 10000 + i * 1000)
                .map(i -> new Gca("" + i, "GCAN" + i, random(data.cellLists, 5, 20)))
                .toList();

        List<String> types = Arrays.asList("VGCS", "VBMS");
        List<String> groups = repeat(50).map(i -> 100 + i * 10).map(i -> "" + i).toList();

        data.gcrefs = data.gcas.stream().flatMap(gca -> {
            return random(groups, 3, 10).stream()
                    .map(group -> Gcref.create(group, random(types), gca, random(gca.celllists,
                            3, 9)));
        }).toList();

        data.ltes = repeat(50).map(i -> 10000 + i * 1000).map(i -> new Lte("" + i, "999", "04", random(data.btss)))
                .toList();
        return data;
    }

    public static Stream<Integer> repeat(int n) {
        return IntStream.range(0, n).boxed();
    }

    public static <T> T random(List<T> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> List<T> random(List<T> list, int size) {
        Random rand = new Random();
        List<T> result = new ArrayList<>();
        int i = 0;
        while (result.size() < size && i < size * 2) {
            i++;
            T t = list.get(rand.nextInt(list.size()));
            if (!result.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static <T> List<T> random(List<T> list, int minSize, int maxSize) {
        Random rand = new Random();
        int size = rand.nextInt(maxSize - minSize) + minSize;
        return random(list, size);
    }

    ////

    @AllArgsConstructor
    @Getter
    public static class Lac {
        String name;
        String lac;
        String mcc;
        String mnc;

        public String getMscId() {
            return String.format("LAC=%s,MCC=%s,MNC=%s", lac, mcc, mnc);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Bsc {
        String name;
        String no;
    }

    @AllArgsConstructor
    @Getter
    public static class Bts {
        String name;
        String no;
        String ci;
        Lac lac;
        Bsc bsc;

        static Bts create(String no, Lac lac, Bsc bsc) {
            return new Bts("BTS" + no, no, no, lac, bsc);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class CellList {
        String id;
        String name;

        List<Bts> btss;

        static CellList create(Bts bts) {
            return new CellList(bts.getNo(), "CLIST" + bts.getNo(), new ArrayList<>(List.of(bts)));
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Gca {
        String code;
        String name;

        List<CellList> celllists;
    }

    @AllArgsConstructor
    @Getter
    public static class Gcref {
        String gcref;
        String groupId;
        String groupName;
        String type;
        Gca gca;
        List<CellList> celllists;

        static Gcref create(String groupId, String type, Gca gca, List<CellList> celllists) {
            return new Gcref(gca.code + groupId, groupId, "GRPNAME" + groupId, type, gca, celllists);
        }

        public String getTypeFullName() {
            return type.equals("VGCS") ? "VOICE GROUP CALL SERVICE" : "VOICE BROADCAST SERVICE";
        }
    }

    @AllArgsConstructor
    @Getter
    static class Lte {
        String eci;
        String emcc;
        String emnc;

        Bts bts;

        static Lte create(String eci, String emcc, String emnc, Bts bts) {
            return new Lte(eci, emcc, emnc, bts);
        }

    }
}