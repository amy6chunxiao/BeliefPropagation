package com.cqupt.bp.model;

public class RealVector {
	protected String label;
	protected String[] valueLabels;
	protected boolean labelsSet;
	protected double[] values;

	public RealVector(int size) throws IndexOutOfBoundsException {
		if (size <= 0)
			throw new IndexOutOfBoundsException("" + size);

		values = new double[size];
		valueLabels = new String[size];
		labelsSet = false;
	} /* constructor RealVector() */

	public RealVector(double[] inputValues) throws IllegalArgumentException {
		if (inputValues == null)
			throw new IllegalArgumentException(
					"RealVector.RealVector(): input array is of wrong length");
		values = new double[inputValues.length];
		valueLabels = new String[inputValues.length];

		for (int i = 0; i < inputValues.length; i++) {
			values[i] = inputValues[i];
		}
	} /* constructor RealVector() */

	public RealVector copy() {
		RealVector output;
		try {
			output = new RealVector(values.length);
			for (int i = 0; i < values.length; i++) {
				output.setValue(i, values[i]);
			}
			output.setLabels(valueLabels);
		} catch (Exception e) {
			System.err.println("RealVector.copy(): bug !!");
			return null;
		}
		return output;
	} /* method copy() */

	public void assign(RealVector v) throws IllegalArgumentException {
		if (v == null || v.getDimension() != getDimension())
			throw new IllegalArgumentException(
					"RealVector.assign(): input vector must be the same size");

		for (int i = 0; i < getDimension(); i++)
			values[i] = v.getValue(i);
	} /* method assign() */

	public int getDimension() {
		return values.length;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel(int index) throws IndexOutOfBoundsException {
		return valueLabels[index];
	} /* method getLabel() */

	public String[] getLabels() {
		return valueLabels;
	}

	public void setLabels(String[] labels) throws IllegalArgumentException {
		if (labels == null || labels.length != valueLabels.length)
			throw new IllegalArgumentException(
					"RealVector.setLabel(): input array is of wrong length");

		for (int i = 0; i < valueLabels.length; i++) {
			if (labels[i] == null) {
				labelsSet = false;
				throw new IllegalArgumentException(
						"RealVector.setLabel(): labels cannot be null");
			}
			valueLabels[i] = labels[i];
		}
		labelsSet = true;
	} /* method setLabels() */

	public void setLabels(String label, String[] labels)
			throws IllegalArgumentException {
		setLabel(label);
		setLabels(labels);
	} /* method setLabels() */

	public double getValue(int index) throws IndexOutOfBoundsException {
		return values[index];
	} /* method getValue() */

	public void setValue(int index, double value)
			throws IndexOutOfBoundsException {
		values[index] = value;
	} /* method setValue() */

	public double getValue(String indexLabel) throws IndexOutOfBoundsException,
			IllegalArgumentException {
		for (int i = 0; i < valueLabels.length; i++) {
			if (indexLabel.equals(valueLabels[i]))
				return values[i];
		}
		throw new IllegalArgumentException(indexLabel);
	} /* method getValue() */

	/**
	 * 设置相应标签的值
	 * 
	 * @param indexLabel
	 * @param value
	 * @throws IndexOutOfBoundsException
	 * @throws IllegalArgumentException
	 */
	public void setValue(String indexLabel, double value)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		for (int i = 0; i < valueLabels.length; i++) {
			if (indexLabel.equals(valueLabels[i])) {
				values[i] = value;
				return;
			}
		}
		throw new IllegalArgumentException(indexLabel);
	} /* method setValue() */

	public boolean isAllOnes() {
		for (int i = 0; i < values.length; i++) {
			if (values[i] != 1)
				return false;
		}
		return true;
	} /* method isAllOnes() */

	public void toAllOnes() {
		for (int i = 0; i < values.length; i++)
			values[i] = 1;
	} /* method toAllOnes() */

	public void toAllZeroes() {
		for (int i = 0; i < values.length; i++)
			values[i] = 0;
	} /* method toAllZeroes() */

	/**
	 * values中每一个数等于原来的数除以总和
	 */
	public void normalise() {
		double sum = 0;
		for (int i = 0; i < values.length; i++)
			sum += values[i];
		if (sum != 0) {
			for (int i = 0; i < values.length; i++)
				values[i] = values[i] / sum;
		}
	} /* method normalise() */

	/**
	 * values[i]=v1.getValue(i)*v2.getValue(i)
	 * 
	 * @param v1
	 * @param v2
	 * @throws IllegalArgumentException
	 */
	public void termProduct(RealVector v1, RealVector v2)
			throws IllegalArgumentException {
		if (v1 == null || v2 == null || v1.getDimension() != v2.getDimension()
				|| v1.getDimension() != values.length)
			throw new IllegalArgumentException(
					"RealVector.termProduct(): parameters length are not compatible");

		for (int i = 0; i < values.length; i++) {
			values[i] = v1.getValue(i) * v2.getValue(i);
		}
	} /* method termProduct() */

	/**
	 * values[i] = x / y
	 * 
	 * @param v1
	 * @param v2
	 * @throws IllegalArgumentException
	 */
	public void divide(RealVector v1, RealVector v2)
			throws IllegalArgumentException {
		if (v1 == null || v2 == null || v1.getDimension() != v2.getDimension()
				|| v1.getDimension() != values.length)
			throw new IllegalArgumentException(
					"RealVector.divide(): parameters length are not compatible");

		for (int i = 0; i < values.length; i++) {
			double x = v1.getValue(i);
			double y = v2.getValue(i);
			if (x == 0 && y == 0)
				values[i] = 0;
			else if (y == 0) {
				throw new IllegalArgumentException(
						"RealVector.divide(): division by zero is not allowed unless numeritor is also zero");
			} else
				values[i] = x / y; 
		}
	} /* method divide() */

	public String toString() {
		String output = "";

		if (labelsSet) {
			output += "( " + valueLabels[0];

			for (int i = 1; i < valueLabels.length; i++) {
				output += ", " + valueLabels[i];
			}
			output += " )\n";
		}

		output += "[ " + values[0];
		for (int i = 1; i < valueLabels.length; i++) {
			output += ", " + values[i];
		}
		output += " ]\n";

		return output;

	} /* method toString() */
} /* class RealVector */
