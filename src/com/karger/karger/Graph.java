package src.com.karger.karger;

import src.com.karger.utils.Product;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class Graph  {

    private static Random rnd;
    private static final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    // Graph parameters
    private final int numProducts;
    private final boolean debug;
    private final boolean weighted;

    // Data structures
    private final boolean[][] buyTogether;
    private final Map<Integer, Product> products = new HashMap<>();
    private final Map<Integer, ArrayList<Product>> vertices;
    private final List<Edge> edges = new ArrayList<>();

    public Graph(int n, boolean debug, boolean weighted) {
        // Parameters
        numProducts = n;
        this.debug = debug;
        this.weighted = weighted;

        // Data structures
        buyTogether = new boolean[n][n];
        vertices = new HashMap<>();

        rnd = new Random();
    }
    
    public void fill() {

        if (debug) System.out.print("[debug] Initializing random products... ");
        //Initialize random products
        for (int i = 0; i < numProducts; i++) {

            products.put(i, new Product(stringRandom(), rnd.nextInt(10) + 1,
                    Math.round(rnd.nextDouble() * 100.0)));

            vertices.put(i, new ArrayList<>());
        }
        if (debug) System.out.println("[OK] DONE.");

        if (debug) System.out.print("[debug] Matching products randomized, generating edges, filling vertices... ");

        // Force vertices to be at least a percentage connected
        while (!sparse()) {
            for (int i = 0; i < numProducts; i++) {
                for (int j = i; j < numProducts; j++) {
                    // A product always is bought with itself
                    if (i == j) {
                        buyTogether[i][j] = true;
                    } else {
                        // If i is bought with j then j is bought with i
                        buyTogether[i][j] = rnd.nextBoolean() | rnd.nextBoolean();
                        buyTogether[j][i] = buyTogether[i][j];

                        if (buyTogether[i][j]) {
                            // Generate edge of the vertices and save in the corresponding structures
                            Edge edge;
                            if (weighted) {
                                double weight = rnd.nextDouble();
                                NumberFormat nf = NumberFormat.getInstance();
                                double number = 0;
                                try {
                                    number = nf.parse(String.format("%.3f", weight)).doubleValue();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                edge = new Edge(products.get(i), products.get(j), number);
                            } else {
                                edge = new Edge(products.get(i), products.get(j));
                            }
                            edges.add(edge);
                            products.get(i).addEdge(edge);
                            products.get(j).addEdge(edge);
                        }
                    }
                }
            }
        }
        if (debug) System.out.println("[OK] DONE.");
    }

    private boolean sparse() {
        double sparse = ((double) edges.size() * 2.0) / (numProducts * (numProducts - 1));
        if (Double.compare(sparse, 0.7) >= 0) {
            return true;
        } else {
            edges.clear();
            for (int i = 0; i < numProducts; i++) {
                products.get(i).getEdges().clear();
            }
            return false;
        }
    }

    /*
     * Return a random string
     */
    public String stringRandom() {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < 8; i++) {
            c = chars[rnd.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    private Edge uniformRandomEdge() {
        double totalWeight = 0;
        for (Edge edge : edges) {
            totalWeight += edge.getWeight();
        }
        int index = -1;
        double random = rnd.nextDouble() * totalWeight;
        for (int i = 0; i < edges.size(); i++) {
            random -= edges.get(i).getWeight();
            if (random <= 0) {
                // We found pounded edge
                //System.out.println(i);
                index = i;
                break;
            }
        }
        return edges.get(index);
    }

    public int minCutKarger() {
        if (debug) System.out.println("[debug] Karger's algorithm in progress...");
        while (vertices.size() > 2) {
            //printGraph();
            if (debug) System.out.println("[debug] Selecting random edge to be removed");
            Edge edgeToRemove = (weighted) ? uniformRandomEdge() : getEdge(rnd.nextInt(edges.size()));
            Product p1 = edgeToRemove.getFirst();
            Product p2 = edgeToRemove.getOppositeEnd(p1);

            if (debug) System.out.print("[debug] Updating vertices status... ");
            edges.removeAll(Collections.singleton(edgeToRemove));
            p1.remove(edgeToRemove);
            p2.remove(edgeToRemove);
            if (debug) System.out.println("[OK] DONE.");

            merge(p1, p2);
        }
        if (debug) System.out.println("[OK] DONE.");
        return edges.size();
    }

    private void merge(Product p1, Product p2) {
        if (debug) System.out.print("[debug] Merging vertices " + getKeyProduct(p1) + " and " + getKeyProduct(p2) + " ");
        vertices.get(getKeyProduct(p1)).add(p2);
        vertices.get(getKeyProduct(p1)).addAll(vertices.get(getKeyProduct(p2)));
        // Migrate all edges to the combined node
        Set<Edge> copy = p2.getEdges();
        for (Iterator<Edge> it = copy.iterator(); it.hasNext(); ) {
            Edge e = it.next();
            it.remove();
            Product p = e.getOppositeEnd(p2);
            // Remove edge from vertices and from product
            int count = Collections.frequency(edges, e);
            edges.removeAll(Collections.singleton(e));
            p2.remove(e);
            p.remove(e);
            // Set new value of edge that no longer exists
            e.replaceEndOfEdge(p2, p1);
            // Add modified edge to vertices
            p1.addEdge(e);
            p.addEdge(e);
            for (int i = 0; i < count; i++) {
                edges.add(e);
            }
        }

        vertices.remove(getKeyProduct(p2));
        if (debug) System.out.println("[OK] DONE.");
    }

    private int getKeyProduct(Product p) {
        for (Map.Entry<Integer, Product> e : products.entrySet()) {
            if (e.getValue().equals(p)) {
                return e.getKey();
            }
        }
        return 0;
    }

    private int getKeyGraph(ArrayList<Product> p) {
        for (Map.Entry<Integer, ArrayList<Product>> e : vertices.entrySet()) {
            if (e.getValue().equals(p)) {
                return e.getKey();
            }
        }
        return 0;
    }

    private Edge getEdge(int i) {
        return ((Edge) edges.toArray()[i]);
    }
    
    public void saveGraph(String sFichero) {
        try {
            File file = new File(sFichero);
			if (!file.exists()) {
                try {
                    // Create File
                    boolean fileCreated = file.createNewFile();
                    // Validate that file actually got created
                    if (!fileCreated) {
                        throw new IOException("Unable to create file at specified path. It already exists");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (debug) System.out.println("[debug] Saving graph to file " + sFichero);

            BufferedWriter bw = new BufferedWriter(new FileWriter(sFichero));
            StringBuilder line = new StringBuilder();

            for (Map.Entry<Integer, ArrayList<Product>> entry : vertices.entrySet()) {
                int i = entry.getKey();
                bw.write(i + ":");

                for (int j = 0; j < products.get(i).getEdges().size(); j++) {
                    line.append(getKeyProduct(products.get(i).getEdge(j).getOppositeEnd(products.get(i))));
                    if (weighted) {
                        line.append("-").append(products.get(i).getEdge(j).getWeight());
                    }
                    line.append(",");
                }

                line.deleteCharAt(line.toString().lastIndexOf(','));
                bw.write(line.toString());
                bw.newLine();
                line.setLength(0);
            }
            bw.close();

            if (debug) System.out.println("[OK] DONE");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void makeCopy(String sFichero, Graph test) {
        File file = new File(sFichero);
        if (file.exists()) {
            try {

                if (debug) System.out.println("[debug] Making copy of graph...");
                Scanner s = new Scanner(file);
                for(int i = 0; i < numProducts; i++){
                    test.vertices.put(i, new ArrayList<>());
                    Product p = new Product(products.get(i).getName(),products.get(i).getUnit(),products.get(i).getPrice());
                    test.products.put(i, p);
                }

                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    String [] nodes = line.split(":");
                    int i = Integer.parseInt(nodes[0]);
                    test.buyTogether[i][i] = true;
                    String [] connections = nodes[1].split(",");

                    int n = 0;
                    for(String connection: connections){
                        int j = (connection.contains("-")) ?
                                    Integer.parseInt(connection.split("-")[0].replaceAll("\\s+","")) :
                                    Integer.parseInt(connection);
                        test.buyTogether[i][j] = true;
                        Edge edge;
                        if (weighted){
                            String [] splitted = connection.split("-");
                            NumberFormat nf = NumberFormat.getInstance();
                            double weight = 0;
                            try {
                                weight = nf.parse(splitted[1]).doubleValue();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            edge = new Edge(test.products.get(i), test.products.get(j), weight);
                        } else{
                            edge = new Edge(test.products.get(i), test.products.get(j));

                        }
                        test.products.get(i).addEdge(edge);
                        test.products.get(j).addEdge(edge);
                        if(j > i){
                            test.edges.add(n, edge);
                            n++;
                        }
                    }


                }
                s.close();
                if (debug) System.out.println("[OK] DONE.");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String edgeToString(Edge edge) {
        return getKeyProduct(edge.getFirst()) + " - " + getKeyProduct(edge.getSecond());
    }

    public void printGraph(){
        System.out.println("GRAPH");
        System.out.println("=====\n");
        StringBuilder stringToPrint = new StringBuilder();

        for (Map.Entry<Integer, ArrayList<Product>> entry : vertices.entrySet()) {
            int i = entry.getKey();
            stringToPrint.append("Node(");
            if (vertices.get(i).isEmpty()){
                stringToPrint.append(i).append(')');
            } else {
                stringToPrint.append(getKeyGraph(vertices.get(i))).append(',');
            }

            for (int j = 0; j < vertices.get(i).size(); j++) {
                stringToPrint.append(getKeyProduct(vertices.get(i).get(j))).append(',');
            }

            if (stringToPrint.toString().lastIndexOf(',') != -1) {
                stringToPrint.deleteCharAt(stringToPrint.toString().lastIndexOf(','));
            }

            if (vertices.get(i).isEmpty()){
                stringToPrint.append(" : [");
            } else {
                stringToPrint.append(") : [");
            }

            for (int j = 0; j < products.get(i).getEdges().size(); j++) {
                stringToPrint.append(getKeyProduct(products.get(i).getEdge(j).getOppositeEnd(products.get(i)))).append(",");
            }

            if (stringToPrint.toString().lastIndexOf(',') != -1) {
                System.out.println(stringToPrint.substring(0, stringToPrint.toString().length() - 1) + "]");
            } else {
                System.out.println(stringToPrint + "]");
            }

            stringToPrint.setLength(0);
        }
        System.out.println();
    }

    public void printInitialGraph(){
        System.out.println("INITIAL GRAPH");
        System.out.println("=============\n");
        StringBuilder stringToPrint = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            stringToPrint.append(i).append(": [");
            for (int j = 0; j < products.get(i).getEdges().size(); j++) {
                stringToPrint.append(getKeyProduct(products.get(i).getEdge(j).getOppositeEnd(products.get(i)))).append(",");
            }

            if (stringToPrint.toString().lastIndexOf(',') != -1) {
                System.out.println(stringToPrint.substring(0, stringToPrint.toString().length() - 1) + "]");
            } else {
                System.out.println(stringToPrint + "]");
            }

            stringToPrint.setLength(0);
        }
    }

    public void printBoughtTogether(){
        System.out.println("numProducts BOUGHT TOGETHER TABLE");
        System.out.println("==============================\n");
        for (boolean[] aBuyTogether : buyTogether) {
            for (int j = 0; j < buyTogether[0].length; j++) {
                int aux = aBuyTogether[j] ? 1 : 0;
                System.out.print(aux + "  ");
            }
            System.out.print("\n");
        }
        System.out.println();
    }

    public void printProducts(){
        System.out.println();
        System.out.println("PRODUCT LIST");
        System.out.println("============\n");
        for (int i = 0; i < products.size(); i++) {
            System.out.println("Product_" + i + ": " + products.get(i).toString());
        }
        System.out.println();
    }

    public void printProductsConnection() {
        for (int i = 0; i < products.size(); i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < products.get(i).getEdges().size(); j++) {
                Edge e = products.get(i).getEdge(j);
                System.out.print(getKeyProduct(e.getFirst()) + " - " + getKeyProduct(e.getOppositeEnd(e.getFirst())) + " ");
            }
            System.out.println();
        }
    }

    public void printEdges() {
        for (Edge edge : edges) {
            System.out.println(edgeToString(edge));
        }
    }
}