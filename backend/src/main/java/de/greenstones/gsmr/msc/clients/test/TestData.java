package de.greenstones.gsmr.msc.clients.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.geotools.referencing.GeodeticCalculator;

import de.greenstones.gsmr.msc.ApplicationException;
import de.greenstones.gsmr.msc.core.IdConverter;
import de.greenstones.gsmr.msc.gis.FeatureProvider;
import de.greenstones.gsmr.msc.gis.SimpleFeatureProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
public class TestData {
    List<Bsc> bscs;
    List<Lac> lacs;
    List<Bts> btss;
    List<CellList> cellLists;
    List<Gca> gcas;
    List<Gcref> gcrefs;
    List<Lte> ltes;

    Map<String, FeatureProvider> featureProviders;

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

        SimpleFeatureProvider cellFeatureProvider = new SimpleFeatureProvider("EPSG:4326");

        cellFeatureProvider.add("10000", 8.631705, 50.172309, 1000.);
        cellFeatureProvider.add("10001", 8.628027, 50.176661, 1500);
        cellFeatureProvider.add("10002", 8.605501, 50.180791, 2000);
        cellFeatureProvider.add("10003", 8.636469, 50.188740, 2000);

        data.featureProviders = Map.of("cells", cellFeatureProvider);

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

        data.lacs = repeat(4).map(i -> 100 * (i + 1))
                .map(i -> new Lac("LAC" + i, IdConverter.leftPad("" + i, 5, '0'), "999", "06")).toList();

        data.btss = repeat(250).map(i -> 10000 + i * 100)
                .map(i -> Bts.create("" + i, random(data.lacs),
                        random(data.bscs)))
                .toList();

        data.cellLists = data.btss.stream().map(bts -> CellList.create(bts)).toList();

        Map<String, Coords> lacsCoords = new HashMap<>();
        data.lacs.stream().forEach(lac -> {
            Coords coords = randomCoords(7.66, 49.9, 9.33, 50.33);
            lacsCoords.put(lac.getLac(), coords);
        });

        SimpleFeatureProvider cellFeatureProvider = new SimpleFeatureProvider("EPSG:4326");

        Map<String, Coords> btsCoords = new HashMap<>();
        data.btss.stream().forEach(bts -> {
            Coords lacCoords = lacsCoords.get(bts.getLac().lac);
            Coords coords = randomCoords(lacCoords, 0.2, 0.1);
            btsCoords.put(bts.ci, coords);
            cellFeatureProvider.add(bts.ci, coords.x, coords.y, 3500);
        });
        // -----

        data.gcas = repeat(50).map(i -> 10000 + i * 1000)
                .map(i -> new Gca("" + i, "GCAN" + i, randomCellLists(data.btss, btsCoords, data.cellLists, 5, 20)))
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

        data.featureProviders = Map.of("cells", cellFeatureProvider);

        return data;
    }

    static record Coords(double x, double y) {
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

    public static List<CellList> randomCellLists(List<Bts> list, Map<String, Coords> coords, List<CellList> clists,
            int minSize,
            int maxSize) {

        Bts bts = random(list);
        Coords c1 = coords.get(bts.ci);

        List<CellList> list2 = coords.keySet().stream().map(k -> {
            Coords c2 = coords.get(k);
            double distance = distance(c1, c2);
            return new Dist(k, distance);
        }).sorted((a, b) -> a.dist.compareTo(b.dist)) //
                .limit(random(minSize, maxSize)) //
                .map(d -> d.ci)//
                .map(ci -> {
                    Optional<CellList> any = clists.stream().filter(cl -> cl.getId().equals("" + ci)).findAny();
                    return any;
                })//
                .filter(f -> !f.isEmpty())
                .map(f -> f.get()).toList();

        // return clists.stream().limit(5).toList();
        return list2;
    }

    static record Dist(String ci, Double dist) {
    }

    public static <T> List<T> random(List<T> list, int minSize, int maxSize) {
        Random rand = new Random();
        int size = rand.nextInt(maxSize - minSize) + minSize;
        return random(list, size);
    }

    public static int random(int minSize, int maxSize) {
        Random rand = new Random();
        int size = rand.nextInt(maxSize - minSize) + minSize;
        return size;
    }

    public static Coords randomCoords(double minX, double minY, double maxX, double maxY) {
        Random rand = new Random();
        double x = rand.nextDouble(maxX - minX) + minX;
        double y = rand.nextDouble(maxY - minY) + minY;
        return new Coords(x, y);
    }

    public static Coords randomCoords(Coords c, double radiusX, double radiusY) {
        return randomCoords(c.x - radiusX, c.y - radiusY, c.x + radiusX, c.y + radiusY);
    }

    public static double distance(Coords c1, Coords c2) {
        GeodeticCalculator calc = new GeodeticCalculator();

        calc.setStartingGeographicPoint(c1.x, c1.y);
        calc.setDestinationGeographicPoint(c2.x, c2.y);

        double distance = calc.getOrthodromicDistance(); // in Metern
        return distance;
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
    @ToString
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