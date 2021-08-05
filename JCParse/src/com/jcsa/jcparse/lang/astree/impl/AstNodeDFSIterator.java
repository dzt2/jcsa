package com.jcsa.jcparse.lang.astree.impl;

import java.util.Stack;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * Access the nodes of AST by Deep-First-Search. A node can be accessed when all
 * its children have been accessed
 *
 * @author yukimula
 */
public class AstNodeDFSIterator implements AstNodeIterator {

	/**
	 * Element of stack to access AST in deep-first-search
	 *
	 * @author yukimula
	 */
	protected static class _Stack_Element {

		/** node of which children are accessed **/
		private AstNode node;
		/** index to the next child of node in element **/
		private int next_index;

		/**
		 * create a stack element for the node
		 *
		 * @param node
		 * @throws Exception
		 */
		protected _Stack_Element(AstNode node) throws Exception {
			if (node == null)
				throw new IllegalArgumentException("Invalid node: null");
			else {
				this.node = node;
				this.next_index = 0;
			}
		}

		/**
		 * get the node of stack element
		 *
		 * @return
		 */
		protected AstNode get_node() {
			return node;
		}

		/**
		 * get the index to the next child of the node
		 *
		 * @return
		 */
		protected int get_next_index() {
			return next_index;
		}

		/**
		 * increment the index to the next child
		 */
		protected void increment_index() {
			next_index++;
		}

		/**
		 * whether there is more child to be accessed in the stack element
		 *
		 * @return
		 */
		protected boolean accessible() {
			return next_index < node.number_of_children();
		}
	}

	/** stack for elements to access deep-first-search **/
	private Stack<_Stack_Element> stack;
	/** the next node to be accessed **/
	private AstNode next_node;

	/**
	 * create an iterator for AST to access nodes in deep-first-search
	 *
	 * @param root
	 * @throws Exception
	 */
	public AstNodeDFSIterator(AstNode root) throws Exception {
		if (root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			stack = new Stack<>();
			stack.push(new _Stack_Element(root));
			this.roll_next(); // roll to first
		}
	}

	/**
	 * roll to the next node for accessed
	 *
	 * @throws Exception
	 */
	private void roll_next() throws Exception {
		next_node = null; // initialization

		/* find the next node to be accessed */
		while (!stack.empty()) {
			/* get the top element of the stack */
			_Stack_Element element = stack.peek();

			/* to the child level */
			if (element.accessible()) {
				/* increment the node */
				AstNode parant = element.get_node();
				int next_index = element.get_next_index();
				element.increment_index();

				/* get child and push it to the stack */
				AstNode child = parant.get_child(next_index);
				if (child != null)
					stack.push(new _Stack_Element(child));
			}
			/* return the top.node and pop it */
			else {
				next_node = element.get_node();
				stack.pop();
				break;
			}
		}
	}

	@Override
	public boolean has_next() {
		return next_node != null;
	}

	@Override
	public AstNode get_next() throws Exception {
		AstNode ans = next_node;
		roll_next();
		return ans;
	}

}
