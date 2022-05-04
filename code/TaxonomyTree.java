
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class TaxonomyTree {
	private Taxon root;
	private Hashtable<String, Taxon> classifications;
	
	/**
	 * 
	 * @author kaytc
	 */
	public static enum Hierarchy {
		ORDER,
		SEMIORDER,
		SUBORDER,
		INFRAORDER,
		SUPERFAMILY,
		FAMILY,
		GENUS,
		SPECIES;
	}
	
	/**
	 * Construct a new TaxonomyTree from an XML Document Object Model (DOM) Node
	 * @param rootNode
	 */
	public TaxonomyTree(Node rootNode) {
		this.root = Taxon.constructTaxonTree(rootNode);
		this.classifications = iterativeSetClassifications(root);
	}
	
	/**
	 * Construct a new TaxonomyTree from a pre-existing Taxon object
	 * @param root
	 */
	public TaxonomyTree(Taxon root) {
		this.root = root;
		this.classifications = iterativeSetClassifications(root);
	}
	
	/**
	 * Recursive traversal of the taxonomyTree and insertion into the classifications instance variable.
	 * @param classes , the Hashtable in question that we are inserting taxons into
	 * @param root , the root Taxon that we will use as the basis for our traversal
	 */
	private static void setClassifications(Hashtable<String, Taxon> classes, Taxon root) {
		classes.put(root.getClade().toUpperCase(), root);
		for (Taxon branch : root.branchList()) {
			setClassifications(classes, branch);
		}
	}
	
	/**
	 * Iterative traversal of the taxonomyTree and insertion into the classifications instance variable.
	 * @param root , the root Taxon that we will use as the basis for our traversal
	 * @return returns a hashtable where the key is a string variable of the taxon's 'taxa' name, and
	 * the value is the Taxon itself.
	 */
	private static Hashtable<String, Taxon> iterativeSetClassifications(Taxon root) {
		if (root == null) {
			throw new IllegalArgumentException("The root taxon cannot be null.");
		}
		
		Hashtable<String, Taxon> classes = new Hashtable<String, Taxon>();
		Stack<Taxon> taxStack = new Stack<Taxon>();
		taxStack.push(root);
		// pre-order traversal of the taxonomyTree and insertion into the hashtable
		while (!taxStack.isEmpty()) {
			Taxon taxAt = taxStack.pop();
			classes.put(taxAt.getClade().toUpperCase(), taxAt);
			// add branches to stack
			for (Taxon branch : taxAt.branchList()) {
				taxStack.push(branch);
			}
		}
		return classes;
	}
	
	/**
	 * Gets a given taxon from the taxonomyTree based on a given string s. If a taxon with a clade/order
	 * name equivalent to that of String s is present, then that taxon is returned. Otherwise, getTaxon()
	 * will return null.
	 * @param s , the name of the taxon you are searching for
	 * @return the Taxon object you searched for, or null if it is not present
	 */
	public Taxon getTaxon(String s) {
		if (s == null) {
			throw new IllegalArgumentException("String s must not be null!");
		}
		return classifications.get(s.toUpperCase());
	}
	
	/**
	 * Returns a list of all Taxons of a certain hierarchy linneanH
	 * @param h
	 * @return
	 */
	public List<Taxon> getTaxonListOfHierarchy(Hierarchy linneanH) {
		Stack<Taxon> taxStack = new Stack<Taxon>();
		taxStack.push(root);
		LinkedList<Taxon> tList = new LinkedList<Taxon>();
		// extra case (could be solved by a HEADER node maybe, or maybe not worth the tradeoff)
		if (linneanH == root.getHierarchy()) {
			tList.add(root);
			return tList;
		}
		
		boolean targetHierarchy = false;
		while (!taxStack.isEmpty()) {
			Taxon taxAt = taxStack.pop();
			targetHierarchy = taxAt.getHierarchy().ordinal() >= linneanH.ordinal() - 1;
			// Add the branches of the current Taxon if we're one hierarchy above the target hier
			for (Taxon branch : taxAt.branchList()) {
				if (targetHierarchy) tList.addFirst(branch);
				else taxStack.push(branch);
			}
		}
		return tList;
	}
	
	/**
	 * Returns a list of all Taxons of a certain hierarchy
	 * @param linneanH
	 * @return
	 */
	public List<Taxon> getTaxonListOfHierarchy(String linneanH) {
		try {
			linneanH = linneanH.toUpperCase();
			Hierarchy h = Hierarchy.valueOf(linneanH);
			return getTaxonListOfHierarchy(h);
		} catch(Exception e) {
			System.out.println("There is no such enum constant named " + linneanH + ".");
			throw e;
		}
	}
}
