package src.com.karger.utils;

import src.com.karger.karger.Edge;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Product {
	
	private final String name;
	private final int unit;
	private final double price;

	private final Set<Edge> edges;

	/*
	 * This class represent a vertex of the graph like a product
	 */
    public Product(String name, int unit, double price){
		this.name = name;
		this.unit = unit;
		this.price = price;
		edges = new HashSet<>();
	}

	public void addEdge(Edge edge) {
        for (int i = 0; i < edges.size(); i++) {
            if (getEdge(i).equals(edge)) {
                return;
            }
        }
        edges.add(edge);
    }

    public void remove(Edge edge) {
        for (int i = 0; i < edges.size(); i++) {
            if (getEdge(i).equals(edge)) {
                edges.remove(edge);
            }
        }
    }

	/*
	 * Return edges that connects this product to others
	 */
    public Set<Edge> getEdges() { return edges; }

    public Edge getEdge(int i) {
        return (Edge) edges.toArray()[i];
    }
	public String getName(){
		return this.name ;
	}
	public Double getPrice(){
		return this.price;
	}
	public int getUnit(){
		return this.unit ;
	}
	public String toString(){

	    return "Name: " + this.name + ", Unit: " + this.unit + ", Price: " + this.price;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return unit == product.unit &&
               Double.compare(product.price, price) == 0 &&
               (Objects.equals(name, product.name));
    }

    @Override
    public int hashCode() {
        long temp;
        int result;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + unit;
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}