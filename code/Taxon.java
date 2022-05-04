import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Taxon {
	private final TaxonomyTree.Hierarchy hierarchy;
	private final String clade;
	private final Taxon precursor;
	private final LinkedList<Taxon> branches;
	
	/**
	 * Taxon constructor for when only the DOM node is known
	 * @param taxaNode
	 */
	private Taxon(Node taxaNode) {
		this(taxaNode, null);
	}
	
	/**
	 * Taxon constructor for when the DOM node and parent Taxon are known
	 * @param taxaNode
	 * @param parentTaxa
	 */
	private Taxon(Node taxaNode, Taxon parentTaxa) {
		this(taxaNode, parentTaxa, TaxonomyTree.Hierarchy.valueOf(taxaNode.getNodeName().toUpperCase()));
	}
	
	/**
	 * Taxon constructor for when the DOM node, parent Taxon, and Hierarchy are known
	 * @param taxaNode
	 * @param parentTaxa
	 * @param linneanHier
	 */
	private Taxon(Node taxaNode, Taxon parentTaxa, TaxonomyTree.Hierarchy linneanHier) {
		hierarchy = linneanHier;
		clade = taxaNode.getAttributes().item(0).getTextContent();
		precursor = parentTaxa;
		branches = setBranches(taxaNode);
	}
	
	/**
	 * 
	 * @param rootNode
	 * @return
	 */
	public static Taxon constructTaxonTree(Node rootNode) {
		return new Taxon(rootNode);
	}
	 
	/**
	 * 
	 * @param taxaNode
	 * @return
	 */
	private LinkedList<Taxon> setBranches(Node taxaNode) {
		NodeList childNodes = taxaNode.getChildNodes();
		LinkedList<Taxon> branchList = new LinkedList<Taxon>();
		final int SKIP_NULLNODES = 2;
		for (int child = childNodes.getLength() - SKIP_NULLNODES; child >= 0; child -= SKIP_NULLNODES) {
			Node currentChildNode = childNodes.item(child);
			NamedNodeMap attributeList = currentChildNode.getAttributes();
			
			if (attributeList != null && attributeList.item(0) != null) {
				branchList.addFirst(new Taxon(currentChildNode, this, 
						TaxonomyTree
						.Hierarchy
						.values()[hierarchy.ordinal() + 1]));
			} else {
				return branchList;
			}
		}
		return branchList;
	}
	
	/*
	private Node setPrecursor(Node taxaNode) {
		if (isNodeNull(taxaNode.getParentNode())) {
			return null;
		} else {
			return taxaNode.getParentNode();
		}
	}
	*/
	
	private boolean isNodeNull(Node someNode) {
		if (someNode.toString().equals("[#document: null]")) {
			return true;
		}
		return false;
	}
	
	public void testTaxon() {
		System.out.println("Linnean Hierarchy " + hierarchy);
		System.out.println("Clade " + clade);
		
		System.out.println(hasPrecursor());
		System.out.println("PRECURSOR " + precursor);
		System.out.println(hasBranches());
		System.out.println("Child nodes: ");
		Iterator<Taxon> it = branches.iterator();
		while (it.hasNext()) {
			Taxon descendant = it.next();
			System.out.println("HIER: " + descendant.hierarchy);
			System.out.println("CLADE: " + descendant.clade);
			System.out.println();
			descendant.testTaxon();
			System.out.println("\n______________________________________________________\n");
		}
	}
	
	/**
	 * Returns whether this Taxon has a precursor or not
	 * @return if this Taxon has a non-null precursor
	 */
	public boolean hasPrecursor() {
		return precursor != null;
	}
	
	/**
	 * Returns whether this Taxon has branches or not
	 * @return if this Taxon has branches or not
	 */
	public boolean hasBranches() {
		return branches.size() > 0;
	}
	
	/**
	 * Return the Linnean Hierarchy of this Taxon
	 * @return the TaxonomyTree.Hierarchy of this Taxon
	 */
	public TaxonomyTree.Hierarchy getHierarchy() {
		return this.hierarchy;
	}
	
	/**
	 * Return the clade name of this Taxon
	 * @return the clade name of this Taxon
	 */
	public String getClade() {
		return this.clade;
	}
	
	/**
	 * Returns a string representation of this object. <br>
	 * The <i>first token</i> is the <i><b>HIERARCHY</b></i> name of this Taxon. <br>
	 * The <i>second token</i> is the <i><b>CLADE/ORDER</b></i> name of this Taxon. <br> <br>
	 * 
	 * The final token is an icon representation of the Taxon, <br>
	 * where the left bracket represents whether the Taxon has a <br>
	 * precursor or not ("{" if yes, "[" if no), and the number <br>
	 * of dashes afterwards represents the number of branches <br>
	 * this taxon leads to. <br> <br>
	 * 
	 * EX: <h><b>Semiorder Haplorrhini {--</b></h><br>
	 * Semiorder is the HIERARCHY, Haplorrhini is the ORDER/CLADE <br>
	 * " { " means the Taxon has a precursor, and " -- " <br>
	 * means that it has 2 branch Taxons.
	 * @return A string representation of this object.
	 */
	public String toString() {
		final char hasPrec = '{';
		final char noPrec = '[';
		final char branchChar = '-';
		
		StringBuilder taxaRep = new StringBuilder();
		taxaRep = hasPrecursor() ? taxaRep.append(hasPrec) : taxaRep.append(noPrec);
		for (int i = 0; i < this.branches.size(); i++) {
			taxaRep.append(branchChar);
		}
		return this.hierarchy + " " + this.clade + " " + taxaRep.toString();
	}
	
	/**
	 * Returns the List of Taxon this Taxon branches out towards
	 * @return
	 */
	public List<Taxon> branchList() {
		return branches;
	}
}
