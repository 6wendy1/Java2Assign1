import java.awt.image.ImageProducer;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    public static class Movie {
        //    Name of the movie
        private String movieName;
        //    Year at which that movie released
        private Integer rsYear;
        //    Certificate earned by that movie
        private List<String> Certificate;
        //    Total runtime of the movie
        private Integer runtime;
        //    Genre of the movie
        private String genre;
        //    Rating of the movie at IMDB site
        private Float rating;
        //    mini story/ summary
        private String overview;
        //    Score earned by the movie
        private Integer Meta_score;
        //    Name of the Director
        private String Director;
        //    Name of the Stars
        private String star1;
        private String star2;
        private String star3;
        private String star4;
        //    Total number of votes
        private Long No_of_votes;
        //    Money earned by that movie
        private Integer gross;

        public Movie(String movieName, Integer rsYear, String genre, String star1, String star2, String star3, String star4,
                     Integer runtime, String overview, Float rating, Integer gross) {
            this.movieName = movieName;
            this.rsYear = rsYear;
            this.genre = genre;
            this.star1 = star1;
            this.star2 = star2;
            this.star3 = star3;
            this.star4 = star4;
            this.runtime = runtime;
            this.overview = overview;
            this.rating = rating;
            this.gross = gross;
        }

        public Integer getRsYear() {
            return rsYear;
        }

        public String getGenre() {
            return genre;
        }

        public String getMovieName() {
            return movieName;
        }

        public String getStar1() {
            return star1;
        }

        public String getStar2() {
            return star2;
        }

        public String getStar3() {
            return star3;
        }

        public String getStar4() {
            return star4;
        }

        public List<List<String>> getStarsList() {
            List<String> starList1 = Stream.of(star1, star2).sorted().collect(Collectors.toList());
            List<String> starList2 = Stream.of(star1, star3).sorted().collect(Collectors.toList());
            List<String> starList3 = Stream.of(star1, star4).sorted().collect(Collectors.toList());
            List<String> starList4 = Stream.of(star2, star3).sorted().collect(Collectors.toList());
            List<String> starList5 = Stream.of(star2, star4).sorted().collect(Collectors.toList());
            List<String> starList6 = Stream.of(star3, star4).sorted().collect(Collectors.toList());
            return Stream.of(starList1, starList2, starList3, starList4, starList5, starList6).collect(Collectors.toList());
        }

        public List<String> getStars(){
            return Stream.of(star1, star2,star3,star4).collect(Collectors.toList());
        }


        public List<String> getMovieGenresList() {
            String[] movieGenres = genre.trim().split(",");
            List<String> movieGenresList = new ArrayList<>();
            for (int i = 0; i < movieGenres.length; i++) {
                movieGenresList.add(movieGenres[i].trim());
            }
            return movieGenresList;
        }

        public Integer getRuntime() {
            return runtime;
        }

        public String getOverview() {
            return overview;
        }

        public Float getRating() {
            return rating;
        }

        public Integer getGross() {
            return gross;
        }
    }

    private List<Movie> movieList = new ArrayList<>();

    public List<Movie> getMovieList() {
        return movieList;
    }

    public static Integer getInteger(String string) {
        String str = string;
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String result = m.replaceAll("").trim();
        Integer result2 = Integer.parseInt(result);
        return result2;
    }

    public MovieAnalyzer(String dataset_path) throws FileNotFoundException, URISyntaxException {
        // create reader
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            // The delimiter of the CSV file
            String DELIMITER = ",";
            // read by line
            String line;
            buf.readLine();//Skip the first line
            while ((line = buf.readLine()) != null) {
                // Split but ignore commas in quotes
                String[] columns = line.trim().split(DELIMITER + "(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
//                System.out.println(columns[1]);
                StringBuilder sb = new StringBuilder(columns[7]);
                if (sb.charAt(0) == '"')
                    sb.delete(0, 1);
                if (sb.charAt(sb.length()-1) == '"')
                    sb.delete(sb.length()-1, sb.length());
//                for (int i = 0; i < sb.length()-1; i++) {
//                    if (sb.charAt(i) == '"' && sb.charAt(i+1) == '"') {
//                        sb.delete(i+1, i+2);
//                    }
//                }
                columns[7] = sb.toString();
                Movie movie;
                if (!columns[15].equals("")) {
                    movie = new Movie(columns[1].replaceAll("\"", ""), Integer.parseInt(columns[2]), columns[5].replaceAll("\"", ""), columns[10], columns[11], columns[12], columns[13],
                            getInteger(columns[4]), columns[7].trim(), Float.parseFloat(columns[6]), Integer.parseInt(columns[15].replaceAll("\"", "").replaceAll(",", "")));
                }
                else {
                    movie = new Movie(columns[1].replaceAll("\"", ""), Integer.parseInt(columns[2]), columns[5].replaceAll("\"", ""), columns[10], columns[11], columns[12], columns[13],
                            getInteger(columns[4]), columns[7].trim(), Float.parseFloat(columns[6]), 0);
                }
                this.movieList.add(movie);
//                System.out.println(Integer.parseInt(columns[14]));
//                this.rsYear.add(columns[1]);
//                System.out.println(rsYear);
                // print by column
//                System.out.println("Movie[" + String.join("; ", columns) + "]");
//                System.out.println(Arrays.stream(columns).toList());
//                rsYear.add(Arrays.stream(columns).toList().indexOf(2));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Map<Integer, Integer> getMovieCountByYear() {
        Map<Integer, Long> ctByYear1 = this.movieList.stream().collect(Collectors.groupingBy(Movie::getRsYear, Collectors.counting()));
        Map<Integer, Integer> ctByYear = new HashMap<>(550);
        Integer key = null;
        Integer integ = null;
        for (Integer integer : ctByYear1.keySet()) {
            // get key
            key = integer;
            // get value
            integ = ctByYear1.get(key).intValue();
            ctByYear.put(key, integ);
        }
        return sortByKeyDescending(ctByYear);
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKeyDescending(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByKey()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    public Map<String, Integer> getMovieCountByGenre() {
        List<String> genres = new ArrayList<>();
        for (int j = 0; j < this.movieList.size(); j++) {
            genres.addAll(movieList.get(j).getMovieGenresList());
        }
        Map<String, Integer> genresCountMap = new HashMap<>();
        for (String string : genres) {
            int i = 1;
            if (genresCountMap.get(string) != null) {
                i = genresCountMap.get(string) + 1;
            }
            genresCountMap.put(string, i);
        }
//        Map<String, Long> ctByGenre1 = movieList.stream().collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()));
//        Map<String, Integer> ctByGenre2 = new HashMap<>(550);
//        String key = null;
//        Integer integ = null;
//        for (String string : ctByGenre1.keySet()) {
//            // get key
//            key = string;
//            // get value
//            integ = ctByGenre1.get(key).intValue();
//            ctByGenre2.put(key, integ);
//        }
        HashMap<String, Integer> genresCountMap2 = genresCountMap.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getValue().equals(o2.getValue())) {
                return o1.getKey().compareTo(o2.getKey());
            } else {
                return o2.getValue() - o1.getValue();
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return genresCountMap2;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        List<List<String>> allStarsList = movieList.get(0).getStarsList();
        for (int j = 1; j < this.movieList.size(); j++) {
            allStarsList.addAll(movieList.get(j).getStarsList());
        }
        Map<List<String>, Integer> starCountMap = new HashMap<>();
        for (List<String> list : allStarsList) {
            int i = 1;
            if (starCountMap.get(list) != null) {
                i = starCountMap.get(list) + 1;
            }
            starCountMap.put(list, i);
        }
        return starCountMap;
    }
//        public List<List<String>> getCoStarCount(){
//        return movieList.get(0).getStarsList();
//    }

    //sorting method for method 4
//    public static HashMap<String, Integer> sorting1(Map<String, Integer> map) {
//        HashMap<String, Integer> sortedMap1 = map.entrySet().stream().sorted((o1, o2) -> {
//            if (o1.getValue().equals(o2.getValue())) {
//                return o1.getKey().compareTo(o2.getKey());
//            } else {
//                return o2.getValue() - o1.getValue();
//            }
//        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//        return sortedMap1;
//    }

//    public static HashMap<String, String> sorting2(Map<String, String> map) {
//        HashMap<String, String> sortedMap2 = map.entrySet().stream().sorted((o1, o2) -> {
//            if (o1.getValue().equals(o2.getValue())) {
//                return o1.getKey().compareTo(o2.getKey());
//            } else {
//                return o2.getKey().length() - o1.getKey().length();
//            }
//        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//        return sortedMap2;
//    }


    public List<String> getTopMovies(int top_k, String by) {
        List<String> getTopByRuntime = new ArrayList<>();
        List<String> getTopByOverview = new ArrayList<>();
        if (by.equals("runtime")) {
            Collections.sort(movieList, (o1, o2) -> {
                if (o1.getRuntime().equals(o2.getRuntime())) {
                    return o1.getMovieName().compareTo(o2.getMovieName());
                } else {
                    return o2.getRuntime() - o1.getRuntime();
                }
            });
            for (int i = 0; i < top_k; i++) {
                getTopByRuntime.add(movieList.get(i).getMovieName());
            }
            return getTopByRuntime;
        } else if (by.equals("overview")) {
            Collections.sort(movieList, (o1, o2) -> {
                if (o1.getOverview().length() == o2.getOverview().length()) {
                    return o1.getMovieName().compareTo(o2.getMovieName());
                } else {
                    return o2.getOverview().length() - o1.getOverview().length();
                }
            });
            for (int i = 0; i < top_k; i++) {
//                System.out.println(movieList.get(i).getMovieName());
//                System.out.println(movieList.get(i).getOverview());
                getTopByOverview.add(movieList.get(i).getMovieName());
            }
            return getTopByOverview;
        } else {
            return null;
        }
    }

    public static HashMap<String, Double> sorting1(Map<String, Double> map) {
        HashMap<String, Double> sortedMap1 = map.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getValue().equals(o2.getValue())) {
                return o1.getKey().compareTo(o2.getKey());
            } else {
                if (o2.getValue() > o1.getValue()){
                    return 1;
                }else{
                    return -1;
                }
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return sortedMap1;
    }
    public static HashMap<String, Double> sorting2(Map<String,Double> map) {
        HashMap<String, Double> sortedMap2 = map.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getValue().equals(o2.getValue())) {
                return o1.getKey().compareTo(o2.getKey());
            } else {
                if (o2.getValue() > o1.getValue()){
                    return 1;
                }else{
                    return -1;
                }
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return sortedMap2;
    }

    public List<String> getTopStars(int top_k, String by) {
        Map<String,Double> rating=new HashMap<>();
        Map<String,Integer> num1=new HashMap<>();
        Map<String,Integer> num2=new HashMap<>();
        Map<String, Long> gross=new HashMap<>();
        if (by.equals("rating")){
            for (Movie movie:movieList){
                for (String string:movie.getStars()){
                    if (rating.containsKey(string)){
                        Double m=(double)movie.getRating();
                        Double n=rating.get(string);
                        Integer num=num1.get(string);
                        rating.put(string,m+n);
                        num1.put(string,num+1);
                    }else{
                        rating.put(string, (double)movie.getRating());
                        num1.put(string,1);
                    }
                }
            }
            Set<String> s = rating.keySet();
            for (String str : s) {
                rating.put(str, rating.get(str)/num1.get(str));
            }
            Set<String> a = sorting1(rating).keySet();
            List<String> sortedStars=new ArrayList<>();
//            for (Map.Entry entry : rating.entrySet()) {
//                System.out.println(entry.getKey() + " " + entry.getValue());
//            }
//            System.out.println("------------------------------------------------------");
            for (String str : a) {
                sortedStars.add(str);
            }
            return sortedStars.subList(0,top_k);
        } else if (by.equals("gross")) {
            for (Movie movie:movieList){
                for (String string:movie.getStars()){
                    if (gross.containsKey(string)){
                        Integer m=movie.getGross();
                        Long n=gross.get(string);
                        Integer num=num2.get(string);
                        if (m != 0) {
                            gross.put(string, m + n);
                            num2.put(string, num + 1);
                        }
                    }else{
                        if (movie.getGross() != 0) {
                            gross.put(string, (long)movie.getGross());
                            num2.put(string, 1);
                        }
                    }
                }
            }
            Set<String> s = gross.keySet();
            Map<String,Double> averageGross=new HashMap<>();
            for (String str : s) {
                averageGross.put(str, ((double)gross.get(str))/num2.get(str));
            }
            Set<String> a = sorting2(averageGross).keySet();
            List<String> sortedStars=new ArrayList<>();
            for (Map.Entry entry : averageGross.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            System.out.println("------------------------------------------------------");
            for (String str : a) {
                sortedStars.add(str);
            }
            return sortedStars.subList(0,top_k);
        }else{
            return null;
        }
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        List<String> searchedMap=new ArrayList<>();
        for (Movie movie:movieList){
            if (movie.getMovieGenresList().contains(genre)&&movie.getRating()>=min_rating&&movie.getRuntime()<=max_runtime){
                searchedMap.add(movie.getMovieName());
            }
        }
        searchedMap.sort((o1, o2) -> {
            return o1.compareTo(o2);
        });
        return searchedMap;
    }


}