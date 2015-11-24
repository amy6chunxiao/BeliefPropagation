package bpTest;

import com.cqupt.bp.algorithm.Loopybp;
import com.cqupt.bp.model.BayesNetwork;
import com.cqupt.bp.model.Matrix;
import com.cqupt.bp.model.PearlNode;
import com.cqupt.bp.model.RealVector;

public class Tester {
	public static void main(String args[]) throws Exception {
		PearlNode rain, sprinkler, watson, holmes, test;

		String[] yesno1 = {"R=y", "R=n"};
		String[] yesno2 = {"S=y", "S=n"};
		String[] yesno3 = {"W=y", "W=n"};
		String[] yesno4 = {"H=y", "H=n"};
//		String[] yesno5 = {"T=y", "T=n"};

		rain = new PearlNode("R", 2, yesno1);
		sprinkler = new PearlNode("S", 2, yesno2);
		watson = new PearlNode("W", 2, yesno3);
		holmes = new PearlNode("H", 2, yesno4);
//		test = new PearlNode("T", 2, yesno5);

		rain.addChild(watson);
		rain.addChild(holmes);
		holmes.addParent(sprinkler);
		watson.addParent(rain);
		holmes.addParent(rain);
//		watson.addChild(test);
//		holmes.addChild(test);

		RealVector v;
		v = rain.getPi();
		v.setValue(0, 0.2);
		v.setValue(1, 0.8);

		v = sprinkler.getPi();
		v.setValue(0, 0.1);
		v.setValue(1, 0.9);

		Matrix Mh_rs, Mw_r;

		double[] array1 = {1, 0, 0.9, 0.1, 1, 0, 0, 1};

		Mh_rs = new Matrix(4, 2, array1);
		String[] mhrs1 = {"R=y", "S=y"};
		String[] mhrs2 = {"R=n", "S=y"};
		String[] mhrs3 = {"R=y", "S=n"};
		String[] mhrs4 = {"R=n", "S=n"};
		String[] mhrs5 = {"H=y"};
		String[] mhrs6 = {"H=n"};
		Mh_rs.setLabel("M_H|R,S");
		Mh_rs.setColumnLabels(0, mhrs5);
		Mh_rs.setColumnLabels(1, mhrs6);
		Mh_rs.setRowLabels(0, mhrs1);
		Mh_rs.setRowLabels(1, mhrs2);
		Mh_rs.setRowLabels(2, mhrs3);
		Mh_rs.setRowLabels(3, mhrs4);

		System.out.println(Mh_rs);
		holmes.setMx_wAll(Mh_rs);

		double[] array2 = {1, 0, 0.2, 0.8};
		Mw_r = new Matrix(2, 2, array2);

		String[] mwr1 = {"R=y"};
		String[] mwr2 = {"R=n"};
		String[] mwr3 = {"W=y"};
		String[] mwr4 = {"W=n"};
		Mw_r.setLabel("M_W|R");
		Mw_r.setColumnLabels(0, mwr3);
		Mw_r.setColumnLabels(1, mwr4);
		Mw_r.setRowLabels(0, mwr1);
		Mw_r.setRowLabels(1, mwr2);

		System.out.println(Mw_r);
		watson.setMx_wAll(Mw_r);

		Matrix Mt_wh;

//		double[] array3 = {0.3, 0.7, 0.9, 0.1, 0.2, 0.8, 0, 1};
//
//		Mt_wh = new Matrix(4, 2, array3);
//		String[] mtwh1 = {"W=y", "H=y"};
//		String[] mtwh2 = {"W=n", "H=y"};
//		String[] mtwh3 = {"W=y", "H=n"};
//		String[] mtwh4 = {"W=n", "H=n"};
//		String[] mtwh5 = {"T=y"};
//		String[] mtwh6 = {"T=n"};
//		Mt_wh.setLabel("M_T|W,H");
//		Mt_wh.setColumnLabels(0, mtwh5);
//		Mt_wh.setColumnLabels(1, mtwh6);
//		Mt_wh.setRowLabels(0, mtwh1);
//		Mt_wh.setRowLabels(1, mtwh2);
//		Mt_wh.setRowLabels(2, mtwh3);
//		Mt_wh.setRowLabels(3, mtwh4);
//
//		System.out.println(Mt_wh);
//		test.setMx_wAll(Mt_wh);
		
		// 将给定节点加入到马尔科夫随机场中
		BayesNetwork mrf = new BayesNetwork();

		
		mrf.add(rain);
		mrf.add(holmes);
		mrf.add(sprinkler);
		mrf.add(watson);
//		mrf.add(test);

		Loopybp loopyRule = new Loopybp();
//		System.out.println("迭代" + loopyRule.updateMessage() + "次后收敛");
		
		
		loopyRule.initializeNetwork(mrf);
		loopyRule.updateMessage();
		holmes.getLambda().setValue("H=n", 0);
		watson.getLambda().setValue("W=n", 0);
		loopyRule.updateMessage();
		System.out.println("r--h"+holmes.getPi());
	}
}