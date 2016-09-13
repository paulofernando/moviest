package br.net.paulofernando.moviest.communication;

public class TempCollectionService {

    public static String getCollection(int collectionId) {
        switch (collectionId) {
            case 1:
                return "{\"title\":\"25 great films you might not have seen before\"," +
                        "\"background_image_url\":\"http://paulofernando.net.br/app/moviest/collection1.jpg\"," +
                        "\"source_url\":\"http://imgur.com/gallery/TGWp6\",\"movies_ids\":[10223,2043,10427,10782," +
                        "55448,13803,32079,10388,11524,70436,19403,9404,11368,11507,8996,25300,6072,56669,25155," +
                        "28455,85564,11963,11494,25500,9623]}";
            case 2:
                return "{\"title\":\"15 best buddy movies\"," +
                        "\"background_image_url\":\"http://paulofernando.net.br/app/moviest/collection2.jpg\"," +
                        "\"source_url\":\"http://www.artofmanliness.com/2009/10/08/the-15-best-buddy-movies/\"," +
                        "\"movies_ids\":[11520,10218,31805,941,642,278,3073,16538,150,983,11414,239,11356,11558,9277]}";
            case 3:
                return "{\"title\":\"The 10 most disturbing movies based on real events\"," +
                        "\"background_image_url\":\"http://paulofernando.net.br/app/moviest/collection3.jpg\"," +
                        "\"source_url\":\"http://www.tasteofcinema.com/2016/the-10-most-disturbing-movies-based-on-real-events/\"," +
                        "\"movies_ids\":[67748,1807,15660,1949,83,3133,10591,84188,10692,25998]}";
        }
        return null;
    }
}
