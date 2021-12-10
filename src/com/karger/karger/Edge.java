package src.com.karger.karger;

import src.com.karger.utils.Product;

import java.util.ArrayList;
import java.util.List;

public class Edge {

	private Product first;
    private Product second;

    private final double weight;

    /**
     * Array list to save both origin and destination vertices of an edge
     */
	private final List<Product> vertices = new ArrayList<>();

    /**
     * Default edge constructor, first and second are just names
     * doesn't set any kind of direction.
     * @param first Product 1 of the edge
     * @param second Product 2 of the edge
     */
    Edge(Product first, Product second) {

	    if (first == null || second == null) {
	        throw new NullPointerException("Neither first or second can be null value.");
        }

		this.first = first;
		this.second = second;

		vertices.add(first);
		vertices.add(second);

		weight = 0;
	}

    /**
     * Edge constructor, first and second are just names
     * doesn't set any kind of direction.
     * @param first Product 1 of the edge
     * @param second Product 2 of the edge
     * @param weight of the edge
     */
    Edge(Product first, Product second, double weight) {

        if (first == null || second == null) {
            throw new NullPointerException("Neither first or second can be null value.");
        }

        this.first = first;
        this.second = second;

        vertices.add(first);
        vertices.add(second);

        this.weight = weight;
    }

	Product getOppositeEnd(Product p) {
	    if (first.equals(p)) {
	        return second;
        } else if (second.equals(p)) {
	        return first;
        } else {
            System.out.println("Edge does not contain specified product: " + this + " " + p);
            return null;
        }
	    //else throw new IllegalArgumentException("Edge does not contain specified product: " + this + " " + p);
	}

    void replaceEndOfEdge(Product oldP, Product newP) {
        if (first.equals(oldP)) {
            first = newP;
        } else if (second.equals(oldP)) {
            second = newP;
        } else {
			throw new IllegalArgumentException("Edge does not contain specified products: " + this + " " + oldP + " " + newP);
		}
    }

	Product getFirst() {
		return first;
	}

	Product getSecond() {
		return second;
	}

    double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return (first.equals(edge.first) && second.equals(edge.second)) ||
               (first.equals(edge.second) && second.equals(edge.first));
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = result + (second != null ? second.hashCode() : 0);
        return result;
    }
}