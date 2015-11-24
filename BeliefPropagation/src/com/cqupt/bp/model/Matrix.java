package com.cqupt.bp.model;

import java.util.*;

public class Matrix {
	protected String label;
	protected double[][] values;
	protected Vector[] rowLabels, colLabels;
	protected boolean labelsSet, rowLabelsSet, colLabelsSet;

	public Matrix(int rows, int columns) throws IndexOutOfBoundsException {
		if (rows <= 0 || columns <= 0) {
			throw new IndexOutOfBoundsException(
					"Matrix.Matrix(): matrix dimensions must be greater than zero");
		}
		values = new double[rows][columns];
		rowLabels = new Vector[rows];
		colLabels = new Vector[columns];
		for (int i = 0; i < rows; i++)
			rowLabels[i] = new Vector();
		for (int i = 0; i < columns; i++)
			colLabels[i] = new Vector();

		labelsSet = false;
		rowLabelsSet = false;
		colLabelsSet = false;
	} /* constructor Matrix */

	public Matrix(int rows, int columns, double[] input)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (rows <= 0 || columns <= 0) {
			throw new IndexOutOfBoundsException(
					"Matrix.Matrix(): matrix dimensions must be greater than zero");
		} else if (input.length != rows * columns) {
			throw new IllegalArgumentException(
					"Matrix.Matrix(): input array size must equal to rows * columns");
		}
		values = new double[rows][columns];
		rowLabels = new Vector[rows];
		colLabels = new Vector[columns];
		for (int i = 0; i < rows; i++)
			rowLabels[i] = new Vector();
		for (int i = 0; i < columns; i++)
			colLabels[i] = new Vector();

		labelsSet = false;
		rowLabelsSet = false;
		colLabelsSet = false;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				values[i][j] = input[i * columns + j];
			}
		}
	} /* constructor Matrix */

	public int getRowDimension() {
		return rowLabels.length;
	}

	public int getColumnDimension() {
		return colLabels.length;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Vector getRowLabels(int index) throws IndexOutOfBoundsException {
		try {
			return rowLabels[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Matrix.getRowLabel():");
		}
	} /* method getRowLabels() */

	public void setRowLabels(int index, String[] rowLabel)
			throws IllegalArgumentException {
		try {
			for (int i = 0; i < rowLabel.length; i++) {
				if (rowLabel[i] == null)
					throw new IllegalArgumentException(
							"Matrix.setRowLabel(): labels cannot be null");
				rowLabels[index].add(rowLabel[i]);
			}
			rowLabelsSet = true;
			if (colLabelsSet)
				labelsSet = true;
		} catch (Exception e) {
			throw new IllegalArgumentException("Matrix.setRowLabel():");
		}
	} /* method setRowsLabel() */

	public Vector getColumnLabels(int index) throws IndexOutOfBoundsException {
		try {
			return colLabels[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Matrix.getColumnLabel():");
		}
	} /* method getColumnLabels() */

	public void setColumnLabels(int index, String[] columnLabel)
			throws IllegalArgumentException {
		try {
			for (int i = 0; i < columnLabel.length; i++) {
				if (columnLabel[i] == null)
					throw new IllegalArgumentException(
							"Matrix.setColumnLabel(): labels cannot be null");
				colLabels[index].add(columnLabel[i]);
			}
			colLabelsSet = true;
			if (rowLabelsSet)
				labelsSet = true;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Matrix.setColumnLabel():");
		}
	} /* method setColumnLabels() */

	public double getValue(int row, int col) throws IndexOutOfBoundsException {
		try {
			return values[row][col];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("(" + row + "," + col + ")");
		}
	} /* method getValue() */

	public void setValue(int row, int col, double value)
			throws IndexOutOfBoundsException {
		try {
			values[row][col] = value;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("(" + row + "," + col + ")");
		}
	} /* method setValue() */

	/*
	 * Returns the first occurance if there are multiple values that satisfy the
	 * criteria
	 */
	public double getValue(String rLabel, String cLabel)
			throws IllegalArgumentException {
		for (int i = 0; i < getRowDimension(); i++) {
			if ((Matrix.partOf(rLabel, rowLabels[i]))) {
				for (int j = 0; j < getColumnDimension(); j++) {
					if ((Matrix.partOf(cLabel, colLabels[j])))
						return values[i][j];
				}
			}
		}
		throw new IllegalArgumentException("Matrix.getValue(" + rLabel + ", "
				+ cLabel + ")");
	} /* method getValue() */

	/**
	 * 
	 * @description 第二个参数中包含要查找的str，则返回true，否则为false
	 * @author liucx
	 * @created 2015-9-15 下午4:07:43
	 * @param str
	 * @param vec
	 * @return
	 */
	public static boolean partOf(String str, Vector vec) {
		if (str == null || vec == null)
			return false;

		for (int i = 0; i < vec.size(); i++) {
			// System.out.println(str +" "+ (String) vec.get(i));
			if (((String) vec.get(i)).equals(str))
				return true;
		}
		return false;
	} /* method partOf() */

	protected boolean partOf(String str[], Vector vec) {
		if (str == null || vec == null)
			return false;

		Outer : for (int k = 0; k < str.length; k++) {
			for (int i = 0; i < vec.size(); i++) {
				if (((String) vec.get(i)).equals(str))
					continue Outer;
			}
			return false;
		}

		return true;
	} /* method partOf() */

	public String toString() {
		String output = "";
		if (label != null)
			output += label + ":\n";
		if (labelsSet) {
			output += "( <" + vecToStr(colLabels[0]) + ">";

			for (int i = 1; i < getColumnDimension(); i++) {
				output += ", <" + vecToStr(colLabels[i]) + ">";
			}
			output += " )\n";
		}

		for (int i = 0; i < getRowDimension(); i++) {
			if (i == 0)
				output += "[ ";
			else
				output += "  ";

			for (int j = 0; j < getColumnDimension(); j++) {
				if (j == 0)
					output += "[";
				try {
					output += getValue(i, j);
				} catch (IndexOutOfBoundsException e) {
					System.err.println("Bug(1) in toString() in Matrix class.");
					return "";
				}
				if (j == (getColumnDimension() - 1))
					output += "]";
				else
					output += " ";
			}

			if (i == (getRowDimension() - 1))
				output += " ] ";
			else
				output += "   ";

			if (labelsSet) {
				try {
					output += "<" + vecToStr(rowLabels[i]) + ">\n";
				} catch (IndexOutOfBoundsException e) {
					System.err.println("Bug(2) in toString() in Matrix class.");
				}
			}
		}
		return output;
	} /* method toString() */

	private String vecToStr(Vector vec) {
		if (vec == null)
			return "";

		String output = (String) vec.get(0);
		for (int i = 1; i < vec.size(); i++) {
			output += ", " + (String) vec.get(i);
		}
		return output;
	} /* method vecToStr */

} /* class Matrix */