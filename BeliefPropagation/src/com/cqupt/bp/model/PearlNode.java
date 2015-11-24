package com.cqupt.bp.model;

import java.util.*;

/*
 *               Wi
 *             |    ^
 * Pi_x(wi)   \|/  /|\   Lambda_x(wi)
 *             '    |
 *               X
 *             |    ^
 * Pi_zj(x)   \|/  /|\   Lambda_zj(x)
 *             '    |
 *               Zj
 */
/**
 * 
 * @author M. Druzdzel changed byLuxingyu{lxy.personal@gmail.com}
 * 
 */
public class PearlNode {
	public RealVector preBel;
	public String nodeLabel;
	public String[] valueLabels;
	public boolean consistentFlag;
	public RealVector bel, pi, lambda;
	public Vector<PearlNode> parents;
	public Vector<PearlNode> children;
	public Matrix Mx_wAll;
	public HashMap pi_zi_x;
	public HashMap lambda_x_wi;

	public PearlNode(String nodeLabel, int states, String[] labels)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (states <= 0)
			throw new IndexOutOfBoundsException(
					"PearlNode constructor: # of states must be > 0");
		else if (nodeLabel == null || labels == null || labels.length != states)
			throw new IllegalArgumentException("PearlNode constructor: (1)");
		bel = new RealVector(states);
		pi = new RealVector(states);
		lambda = new RealVector(states);
		lambda.toAllOnes();
		parents = new Vector();
		children = new Vector();
		pi_zi_x = new HashMap();
		lambda_x_wi = new HashMap();
		valueLabels = new String[states];
		this.nodeLabel = nodeLabel;

		for (int i = 0; i < states; i++) {
			if (labels[i] == null)
				throw new IllegalArgumentException("PearlNode constructor: (2)");
			this.valueLabels[i] = labels[i];
		}

		bel.setLabels(nodeLabel, valueLabels);
		pi.setLabels(nodeLabel, valueLabels);
		lambda.setLabels(nodeLabel, valueLabels);
		consistentFlag = false;

	} /* constructor PearlNode() */

	public void setLabel(String str) {
		nodeLabel = str;
	}

	public String getLabel() {
		return nodeLabel;
	}

	public RealVector getBEL() {
		return bel;
	}

	public RealVector getPi() {
		return pi;
	}

	public RealVector getLambda() {
		return lambda;
	}

	public Vector getParents() {
		return parents;
	}

	public Vector getChildren() {
		return children;
	}

	public Matrix getMx_wAll() {
		return Mx_wAll;
	}

	public void setMx_wAll(Matrix M) {
		Mx_wAll = M;
	}

	public boolean isParent(PearlNode node) {
		return parents.contains(node);
	}

	public boolean isChild(PearlNode node) {
		return children.contains(node);
	}

	public boolean isConsistent() {
		return consistentFlag;
	}

	public void dirty() {
		consistentFlag = false;
	}

	public RealVector getPi_zi_x(PearlNode child) {
		return (RealVector) pi_zi_x.get(child);
	}

	public RealVector getLambda_x_wi(PearlNode parent) {
		return (RealVector) lambda_x_wi.get(parent);
	}

	public void addParent(PearlNode node) {
		if ((this != node) && !isParent(node)) {
			parents.add(node);
			node.addChild(this);
		}
	} /* method addParent() */

	public void addChild(PearlNode node) {
		if ((this != node) && !isChild(node)) {
			children.add(node);
			node.addParent(this);
		}
	} /* method addChild() */

	public void updateBEL() {
		System.out.println(nodeLabel + ": updateBEL");
		try {
			bel.termProduct(pi, lambda);
			bel.normalise();
		} catch (IllegalArgumentException e) {
			System.err.println("PearlNode.updateBEL(): bug!!");
		}
	} /* method updateBEL() */

	public void updatePi() {
		System.out.println(nodeLabel + ": updatePi");
		if (parents.size() == 0)
			return;

		pi.toAllZeroes();
		PearlNode parent;
		RealVector pi_z;

		try {

			// System.out.println(pi_z);
			for (int j = 0; j < Mx_wAll.getRowDimension(); j++) {
				double tmp[] = new double[Mx_wAll.getColumnDimension()];

				for (int m = 0; m < Mx_wAll.getColumnDimension(); m++) {
					tmp[m] = Mx_wAll.getValue(j, m);
					System.out.print(tmp[m] + " ");
				}
				System.out.println();
				for (int i = 0; i < parents.size(); i++) {
					parent = (PearlNode) parents.get(i);
					// System.out.println(i +" "+ j);
					pi_z = parent.getPi_zi_x(this);
					for (int k = 0; k < pi_z.getDimension(); k++) {
						// System.out.println(j +" "+ k + " "+ pi.getValue(j)
						// +" "+ pi_z.getLabel(k) +" "+ pi.getLabel(j));
						if (Matrix.partOf(pi_z.getLabel(k),
								Mx_wAll.getRowLabels(j))) {
							for (int m = 0; m < Mx_wAll.getColumnDimension(); m++) {
								tmp[m] *= pi_z.getValue(k);
							}
						}

					}
				}
				for (int m = 0; m < Mx_wAll.getColumnDimension(); m++) {
					pi.setValue(m, pi.getValue(m) + tmp[m]);
					System.out.print(tmp[m] + " ");
				}
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("PearlNode.updatePi: bug in here (1)!!");
			return;
		}

	} /* method updatePi() */

	public void updateLambda() {
		System.out.println(nodeLabel + ": updateLambda");
		if (children.size() == 0)
			return;

		lambda.assign(((PearlNode) children.get(0)).getLambda_x_wi(this));

		for (int i = 1; i < children.size(); i++) {
			PearlNode child = (PearlNode) children.get(i);
			try {
				lambda.termProduct(lambda, child.getLambda_x_wi(this));
			} catch (IllegalArgumentException e) {
				System.err.println("PearlNode.updateLambda: bug in here!!");
				return;
			}
		}
	} /* method updateLambda() */

	public void updatePiZ(PearlNode child) {
		System.out
				.println(nodeLabel + "-> " + child.getLabel() + ": updatePiZ");
		if (!isChild(child)) {
			System.err.println("PearlNode.updatePiZ: input is not a child");
			return;
		}

		RealVector pi_z = null;

		try {
			if ((pi_z = (RealVector) pi_zi_x.get(child)) == null) {
				pi_z = pi.copy();
			}
		} catch (Exception e) {
			System.err.println("PearlNode.updatePiZ: bug in here!! (1)");
		}

		if (children.size() == 1) {
			pi_z = pi.copy();
			pi_z.normalise();
			pi_zi_x.put(child, pi_z);
			// System.out.println(getPi_zi_x(child));
			return;
		}

		try {
			pi_z.divide(bel, child.getLambda_x_wi(this));
		} catch (Exception e) {
			System.err.println("PearlNode.updatePiZ: bug in here!! (2)");
			return;
		}

		pi_z.normalise();
		pi_zi_x.put(child, pi_z);
	} /* method updatePiZ() */

	public void updateLambdaX(PearlNode parent) {
		System.out.println(nodeLabel + "-> " + parent.getLabel()
				+ ": updateLambdaX");
		if (!isParent(parent)) {
			System.err
					.println("PearlNode.updateLambdaX: input is not a parent");
			return;
		}

		RealVector lambda_wi;

		try {
			if ((lambda_wi = getLambda_x_wi(parent)) == null) {
				lambda_wi = new RealVector(parent.getBEL().getDimension());
				lambda_wi.setLabels(parent.bel.getLabels());
			}
		} catch (Exception e) {
			System.err.println("PearlNode.updateLambdaX: bug in here (1)!!");
			return;
		}

		if (lambda.isAllOnes()) {
			lambda_wi.toAllOnes();
			lambda_x_wi.put(parent, lambda_wi);
			return;
		}

		lambda_wi.toAllZeroes();

		for (int i = 0; i < Mx_wAll.getRowDimension(); i++) {
			double tmp = 0;
			for (int k = 0; k < Mx_wAll.getColumnDimension(); k++) {
				tmp += Mx_wAll.getValue(i, k)
						* lambda.getValue((String) Mx_wAll.getColumnLabels(k)
								.get(0));
				System.out.print(tmp + " ");
			}

			for (int j = 0; j < parents.size(); j++) {
				PearlNode parent2 = (PearlNode) parents.get(j);
				if (parent != parent2) {
					try {
						RealVector pi_z = parent2.getPi_zi_x(this);
						for (int m = 0; m < pi_z.getDimension(); m++) {
							if (Matrix.partOf(pi_z.getLabel(m),
									Mx_wAll.getRowLabels(i)))
								tmp *= pi_z.getValue(m);
						}
					} catch (Exception e) {
						System.err
								.println("PearlNode.updateLambdaX: bug in here (2)!!");
						return;
					}
				}
			}

			for (int n = 0; n < lambda_wi.getDimension(); n++) {
				if (Matrix.partOf(lambda_wi.getLabel(n),
						Mx_wAll.getRowLabels(i))) {

					lambda_wi.setValue(n, lambda_wi.getValue(n) + tmp);
					System.out.println(tmp + "_ " + lambda_wi.getLabel(n));
					break;
				}
			}
		}

		try {
			lambda_x_wi.put(parent, lambda_wi);
		} catch (Exception e) {
			System.err.println("PearlNode.updateLambdaX: bug in here!! (3)");
			return;
		}

	} /* method updateLambdaX() */

	/**
	 * 
	 * @description 批量更新该节点到其所有子节点的消息pi
	 * @author liucx
	 * @created 2015-9-14 下午3:30:24
	 */
	public void updatePiZBatch() {
		for (PearlNode child : children) {
			this.updatePiZ(child);
		}
	}

	/**
	 * 
	 * @description 批量更新该节点到其所有父节点的消息lambda
	 * @author liucx
	 * @created 2015-9-14 下午3:33:36
	 */
	public void updateLambdaXBatch() {
		for (PearlNode parent : parents) {
			this.updateLambdaX(parent);
		}
	}

	/**
	 * 
	 * @description 批量初始化父节点到子节点的消息，在lbp中全部初始化为1
	 * @author liucx
	 * @created 2015-9-14 下午4:44:41
	 */
	public void initPiZBatch(boolean isAllOnes) {
		RealVector pi_z;
		for (PearlNode child : children) {
			if ((pi_z = (RealVector) pi_zi_x.get(child)) == null) {
				pi_z = pi.copy();
			}
			if (isAllOnes)
				pi_z.isAllOnes();
			pi_zi_x.put(child, pi_z);
		}
	}

	/**
	 * 
	 * @description 批量初始化子节点到父节点的消息，在lbp中全部初始化为1
	 * @author liucx
	 * @created 2015-9-14 下午4:56:26
	 * @param value
	 */
	public void initLambdaXBatch() {
		RealVector lambda_wi;
		for (PearlNode parent : parents) {
			if ((lambda_wi = getLambda_x_wi(parent)) == null) {
				lambda_wi = new RealVector(parent.getBEL().getDimension());
				lambda_wi.setLabels(parent.bel.getLabels());
			}
			lambda_wi.toAllOnes();
			lambda_x_wi.put(parent, lambda_wi);
		}
	}

	/**
	 * 
	 * @description 计算疑似边相邻两次计算误差,用于判断lbp迭代时每个点是否收敛
	 * @author liucx
	 * @created 2015-9-14 下午3:19:44
	 * @return
	 */
	public double computeTwoBelDifference() {

		if (preBel.getDimension() != bel.getDimension())
			System.err.println("bug in here! bel length error!");

		double difference = 0;
		for (int i = 0; i < preBel.getDimension(); i++) {
			difference += Math.pow(preBel.getValue(i) - bel.getValue(i), 2);
		}
		return Math.sqrt(difference);
	}

	public void setPreBel(RealVector preBel) {
		this.preBel = preBel;
	}

	public RealVector getPreBel() {
		return preBel;
	}

	public HashMap<PearlNode, RealVector> getPi_zi_x() {
		return pi_zi_x;
	}

	public HashMap<PearlNode, RealVector> getLambda_x_wi() {
		return lambda_x_wi;
	}

	/**
	 * changed by Luxingyu{lxy.personal@gmail.com}
	 */
	int indeficationCode;
	public void reflash(int indeficationCode) {
		// 更新边缘概率
		updateBEL();

		for (PearlNode p : parents) {

		}

		for (PearlNode p : children) {

		}
	}/* method reflash */

} /* class PearlNode */