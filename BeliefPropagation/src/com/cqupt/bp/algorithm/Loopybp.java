package com.cqupt.bp.algorithm;

import java.util.List;

import com.cqupt.bp.model.BayesNetwork;
import com.cqupt.bp.model.PearlNode;

/**
 * lbp迭代过程，main函数是调用过程
 * 
 * @author LiuChunxiao
 * 
 */
public class Loopybp {

	// 阈值，前后两次消息之差小于该值则收敛,默认为0.0001
	private double threshold = 0.0001;
	private BayesNetwork mrf;

	public Loopybp() {

	}
	/**
	 * @param threshold
	 *            自定义的阈值
	 */
	public Loopybp(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * 初始化网络中传递的消息
	 * 
	 * @param nodes
	 *            网络节点
	 */
	public void initializeNetwork(BayesNetwork mrf) {

		// 将所有节点到子节点的pi和到父节点的lambda初始化为1
		this.mrf = mrf;

		for (PearlNode node : mrf) {
			node.initPiZBatch(true);
			node.initLambdaXBatch();
		}
		// 由于根节点的边缘概率等于自身的pi
		// 根节点传给每个子节点的消息等于根节点的pi
		List<PearlNode> roots = mrf.getRoots();
		for (PearlNode root : roots) {
			root.initPiZBatch(false);
		}
	}

	/**
	 * 更新消息，第一次更新消息前必须初始化网络节点
	 * 
	 * @return 迭代多少次后收敛
	 */
	public int updateMessage() {

		// 统计迭代次数
		int loopCount = 0;
		while (true) {

			System.out.println("第" + (loopCount + 1) + "次迭代");

			// 保存以前的BEL为preBel
			// 更新自身的lambda，pi，Bel
			for (int i = 0; i < mrf.size(); i++) {

				PearlNode tmp = mrf.get(i);
				tmp.setPreBel(tmp.getBEL().copy());

				tmp.updateLambda();
				tmp.updatePi();
				tmp.updateBEL();

				System.out
						.println("Bel:" + tmp.getLabel() + "=" + tmp.getBEL());

				tmp.updatePiZBatch();
				tmp.updateLambdaXBatch();
			}

			// 判断每个点是否收敛，如果全部收敛，计算完成
			int coverageCount = 0;
			for (PearlNode node : mrf) {
				if (node.computeTwoBelDifference() > threshold)
					break;
				coverageCount++;
			}

			loopCount++;
			if (coverageCount == mrf.size())
				break;
		}
		return loopCount;
	}
}
