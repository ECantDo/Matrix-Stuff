import java.util.Arrays;

public class Matrix {
	private final BigFraction[][] matrix;
	private BigFraction[][] inverseMatrix;

	//================================================================================================================//
	//                                              Constructors
	//================================================================================================================//
	public Matrix(double[][] matrix) {
		this.matrix = new BigFraction[matrix.length][];
		int rowLength = matrix[0].length;
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i].length != rowLength) {
				throw new IllegalArgumentException("All rows must have the same length");
			}
			this.matrix[i] = new BigFraction[matrix[i].length];
			for (int j = 0; j < matrix[i].length; j++) {
				this.matrix[i][j] = BigFraction.valueOf(matrix[i][j]);
			}
		}
	}

	public Matrix(BigFraction[][] matrix) {
		this.matrix = new BigFraction[matrix.length][];
		int rowLength = matrix[0].length;
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i].length != rowLength) {
				throw new IllegalArgumentException("All rows must have the same length");
			}
			this.matrix[i] = new BigFraction[matrix[i].length];
			System.arraycopy(matrix[i], 0, this.matrix[i], 0, matrix[i].length);
		}
	}

	/**
	 * Creates an empty matrix with the given rows and columns
	 *
	 * @param rows    The number of rows
	 * @param columns The number of columns
	 */
	public Matrix(int rows, int columns) {
		this.matrix = new BigFraction[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.matrix[i][j] = new BigFraction(0);
			}
		}
	}

	/**
	 * Creates a matrix filled with the given value
	 *
	 * @param rows    The number of rows
	 * @param columns The number of columns
	 * @param value   The value
	 */
	public Matrix(int rows, int columns, double value) {
		this(rows, columns);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.matrix[i][j] = BigFraction.valueOf(value);
			}
		}
	}

	/**
	 * Creates a square matrix with the given side length
	 *
	 * @param length The side length
	 */
	public Matrix(int length) {
		this(length, length);
	}

	/**
	 * Creates a matrix with the given size
	 *
	 * @param size The size as a two-element int array -> [rows, columns]
	 */
	public Matrix(int[] size) {
		this(size[0], size[1]);
	}

	//================================================================================================================//
	//                                          Static Constructors
	//================================================================================================================//

	/**
	 * Creates an identity matrix of the given size
	 *
	 * @param n The size
	 * @return A new NxN identity matrix
	 */
	public static Matrix identity(int n) {
		Matrix matrix = new Matrix(n, n);
		for (int i = 0; i < n; i++) {
			matrix.matrix[i][i] = new BigFraction(1);
		}
		return matrix;
	}

	public static Matrix ZERO(int width, int height) {
		return new Matrix(width, height);
	}

	//================================================================================================================//
	//                                       Matrix Manipulation Methods
	//================================================================================================================//

	/**
	 * Calculates the scaled matrix -- multiples each value in the matrix by the given value
	 *
	 * @param value The value
	 * @return The matrix scaled by the given value
	 */
	public Matrix scale(double value) {

		BigFraction bdValue = new BigFraction(value);
		Matrix matrix = new Matrix(this.size());
		for (int i = 0; i < matrix.matrix.length; i++) {
			for (int j = 0; j < matrix.matrix[i].length; j++) {
				matrix.matrix[i][j] = this.matrix[i][j].multiply(bdValue);
			}
		}
		return matrix;
	}

	/**
	 * Adds two matrices, and returns a new matrix.
	 * Matrices must be of the same size
	 *
	 * @param other The other matrix to add
	 * @return The new summed matrix
	 */
	public Matrix add(Matrix other) {
		if (!Arrays.equals(this.size(), other.size())) {
			throw new IllegalArgumentException("Cannot add matrices of different sizes");
		}

		Matrix matrix = new Matrix(this.size());
		for (int i = 0; i < matrix.matrix.length; i++) {
			for (int j = 0; j < matrix.matrix[i].length; j++) {
				matrix.matrix[i][j] = this.matrix[i][j].add(other.matrix[i][j]);
			}
		}
		return matrix;
	}

	/**
	 * Subtracts two matrices, and returns a new matrix.
	 * Matrices must be of the same size
	 *
	 * @param other The other matrix
	 * @return The new matrix with the subtracted values
	 */
	public Matrix subtract(Matrix other) {
		if (!Arrays.equals(this.size(), other.size())) {
			throw new IllegalArgumentException("Cannot subtract matrices of different sizes");
		}

		Matrix matrix = new Matrix(this.size());
		for (int i = 0; i < matrix.matrix.length; i++) {
			for (int j = 0; j < matrix.matrix[i].length; j++) {
				matrix.matrix[i][j] = this.matrix[i][j].subtract(other.matrix[i][j]);
			}
		}
		return matrix;
	}

	public Matrix multiply(Matrix other) {
		if (this.columns() != other.rows()) {
			throw new IllegalArgumentException("Cannot multiply matrices of non compatible sizes\n" +
					"Expected other matrix to have " + this.columns() + " rows\n");
		}

		Matrix matrix = new Matrix(this.rows(), other.columns());
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				BigFraction value = new BigFraction(0);
				for (int k = 0; k < this.columns(); k++) {
					value = value.add(this.matrix[i][k].multiply(other.matrix[k][j]));
				}
				matrix.matrix[i][j] = value;
			}
		}
		return matrix;
	}

	public Matrix transpose() {
		Matrix matrix = new Matrix(this.columns(), this.rows());
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				matrix.matrix[i][j] = this.matrix[j][i];
			}
		}
		return matrix;
	}

	/**
	 * Rounding now reduces the fraction
	 */
	public void simplify() {
		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = 0; j < this.matrix[i].length; j++) {
				this.matrix[i][j] = this.matrix[i][j].reduce();
			}
		}
	}

	/**
	 * Reduces the matrix into row echelon form.
	 * Preforms the same operations onto the other matrix
	 *
	 * @param otherMatrix The other matrix to perform the operations upon
	 * @return The reduced matrix and the other matrix
	 */
	public Matrix[] reduce(Matrix otherMatrix) {

		// Make a copy of the matrix to perform the operations on
		Matrix matrix = this.copy();

		// Make a copy of the other matrix, if there is one
		Matrix other = otherMatrix != null ? otherMatrix.copy() : new Matrix(this.rows(), 1);

		if (this.rows() != other.rows()) {
			throw new IllegalArgumentException("Cannot reduce matrices of different row sizes");
		}

		// get a non-zero along the main diagonal
		for (int i = 0; i < matrix.rows(); i++) {
			if (matrix.matrix[i][i].equals(BigFraction.ZERO)) {
				for (int j = i + 1; j < matrix.rows(); j++) {
					if (!matrix.matrix[j][i].equals(BigFraction.ZERO)) {
						matrix.swapRows(i, j);
						other.swapRows(i, j);
						break;
					}
				}
			}
		}


//		System.out.println("Reducing...");
//		System.out.println(matrix);
		for (int rowA = 0; rowA < matrix.columns() - 1; rowA++) {
			for (int rowB = rowA + 1; rowB < matrix.rows(); rowB++) {
				BigFraction multValue = matrix.matrix[rowB][rowA]
						.divide(matrix.matrix[rowA][rowA])
						.multiply(new BigFraction(-1)).reduce();
				matrix.addRows(rowB, rowA, multValue);
				other.addRows(rowB, rowA, multValue);
				matrix.simplify();
				other.simplify();
			}
//			System.out.println(matrix);
		}

//		System.out.println("Simplifying");
		for (int i = 0; i < matrix.rows(); i++) {
			BigFraction multValue = BigFraction.ONE
					.divide(matrix.matrix[i][i]);
//			BigFraction multValue = new BigFraction(1, matrix.matrix[i][i]);
			matrix.multiplyRow(i, multValue);
			other.multiplyRow(i, multValue);
			matrix.simplify();
			other.simplify();
//			System.out.println(matrix);
		}

		return new Matrix[]{matrix, otherMatrix != null ? other : null};
	}

	public Matrix[] reducedRowEchelonForm(Matrix otherMatrix) {
		// Make a copy of the matrix to perform the operations on
		Matrix matrix = this.copy();

		// Make a copy of the other matrix, if there is one
		Matrix other = otherMatrix != null ? otherMatrix.copy() : new Matrix(this.rows(), 1);

		if (this.rows() != other.rows()) {
			throw new IllegalArgumentException("Cannot reduce matrices of different row sizes");
		}

		Matrix[] reduced = matrix.reduce(other);
		matrix = reduced[0];
		other = reduced[1];

		for (int rowA = matrix.rows() - 1; rowA >= 0; rowA--) {
			for (int rowB = rowA - 1; rowB >= 0; rowB--) {
				BigFraction multValue = matrix.matrix[rowB][rowA].multiply(-1).reduce();
				matrix.addRows(rowB, rowA, multValue);
				other.addRows(rowB, rowA, multValue);

				matrix.simplify();
				other.simplify();
			}
		}

		matrix.simplify();
		other.simplify();

		return new Matrix[]{matrix, otherMatrix != null ? other : null};
	}

	public Matrix solve(Matrix other) {
		return this.reducedRowEchelonForm(other)[1];
	}

	/**
	 * Returns the determinant of the matrix
	 *
	 * @return The determinant (BigFraction)
	 */
	public BigFraction determinant() {
		if (!this.isSquare()) {
			throw new IllegalArgumentException("Determinant is only defined for square matrices");
		}

		// Copy the matrix because we don't want to modify the original matrix
		Matrix matrix = this.copy();
		BigFraction det = BigFraction.ONE;
		int swaps = 0;

		// Perform Gaussian elimination to transform the matrix into an upper triangular matrix
		for (int i = 0; i < matrix.rows(); i++) {
			// If the diagonal element is zero, we need to swap with a lower row
			if (matrix.matrix[i][i].equals(BigFraction.ZERO)) {
				boolean swapped = false;
				for (int j = i + 1; j < matrix.rows(); j++) {
					if (!matrix.matrix[j][i].equals(BigFraction.ZERO)) {
						matrix.swapRows(i, j);
						swaps++;
						swapped = true;
						break;
					}
				}
				if (!swapped) {
					// If no non-zero pivot is found, the determinant is zero
					return BigFraction.ZERO;
				}
			}

			// Now perform row reduction
			for (int j = i + 1; j < matrix.rows(); j++) {
				if (!matrix.matrix[j][i].equals(BigFraction.ZERO)) {
					BigFraction factor = matrix.matrix[j][i].divide(matrix.matrix[i][i]);
					matrix.addRows(j, i, factor.negate());
				}
			}
		}

		// The determinant is the product of the diagonal elements
		for (int i = 0; i < matrix.rows(); i++) {
			det = det.multiply(matrix.matrix[i][i]);
		}

		// Account for row swaps (each swap flips the sign of the determinant)
		if (swaps % 2 != 0) {
			det = det.negate();
		}

		return det;
	}

	/**
	 * Gets the minor of a matrix at a given row and column
	 * M(row, column) = determinant(matrix.removeRow(row).removeColumn(column))
	 *
	 * @param row    Row to remove
	 * @param column Column to remove
	 * @return The minor
	 */
	public BigFraction minor(int row, int column) {
		return this.removeRow(row).removeColumn(column).determinant();
	}

	/**
	 * Gets the cofactor of a matrix at a given row and column
	 * C(row, column) = (-1)^(row + column) * M(row, column)
	 *
	 * @param row    Row to remove
	 * @param column Column to remove
	 * @return The cofactor
	 */
	public BigFraction cofactor(int row, int column) {
		return this.minor(row, column).multiply(new BigFraction((row + column) % 2 == 0 ? 1 : -1));
	}

	/**
	 * Returns the matrix of cofactors
	 * C(row, column) = (-1)^(row + column) * M(row, column)
	 *
	 * @return The cofactor matrix
	 */
	public Matrix cofactor() {
		Matrix matrix = new Matrix(this.rows(), this.columns());
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				matrix.matrix[i][j] = this.cofactor(i, j);
			}
		}
		return matrix;
	}

	/**
	 * Returns the adjugate matrix;
	 * Transpose the cofactor matrix
	 *
	 * @return The adjugate matrix
	 */
	public Matrix adjugate() {
		return this.cofactor().transpose();
	}

	public Matrix inverse() {
		return this.reducedRowEchelonForm(Matrix.identity(this.rows()))[1];
	}


	//================================================================================================================//
	//                                     Elementary Row Operations
	//================================================================================================================//

	/**
	 * Swaps two rows in the matrix
	 *
	 * @param row1 First row
	 * @param row2 Second row
	 */
	private void swapRows(int row1, int row2) {
		BigFraction[] temp = this.matrix[row1];
		this.matrix[row1] = this.matrix[row2];
		this.matrix[row2] = temp;
	}

	/**
	 * Multiplies a row by a value.
	 * Values must not be 0
	 *
	 * @param row   Row to be multiplied
	 * @param value Value to multiply by
	 */
	private void multiplyRow(int row, BigFraction value) {
		if (value.equals(BigFraction.ZERO)) {
			throw new IllegalArgumentException("Cannot multiply row by 0");
		}
		for (int i = 0; i < matrix[row].length; i++) {
			matrix[row][i] = matrix[row][i].multiply(value);
		}
	}

	private void multiplyRow(int row, double value) {
		if (value == 0) {
			throw new IllegalArgumentException("Cannot multiply row by 0");
		}
		this.multiplyRow(row, new BigFraction(value));
	}


	/**
	 * Adds a row to the multiple of another row.
	 * Value can technically be 0, but it does nothing
	 *
	 * @param rowA  First row
	 * @param rowB  Second row
	 * @param value Value
	 */
	private void addRows(int rowA, int rowB, BigFraction value) {
		for (int col = 0; col < matrix[rowA].length; col++) {
			matrix[rowA][col] = matrix[rowA][col].add(matrix[rowB][col].multiply(value));
		}
	}

	private void addRows(int row1, int row2, double value) {
		this.addRows(row1, row2, new BigFraction(value));
	}

	//================================================================================================================//
	//                                       Matrix Access Methods
	//================================================================================================================//

	/**
	 * Returns the value at the given row and column
	 *
	 * @param row    The row
	 * @param column The column
	 * @return The value
	 */
	public BigFraction get(int row, int column) {
		return matrix[row][column].reduce();
	}

	/**
	 * Sets the value at the given row and column
	 *
	 * @param row    The row
	 * @param column The column
	 * @param value  The value
	 */
	public void set(int row, int column, double value) {
		this.set(row, column, new BigFraction(value));
	}

	public void set(int row, int column, BigFraction value) {
		matrix[row][column] = value;
	}

	/**
	 * Returns the number of rows
	 *
	 * @return The number of rows
	 */
	public int rows() {
		return matrix.length;
	}

	/**
	 * Returns the number of columns
	 *
	 * @return The number of columns
	 */
	public int columns() {
		return matrix[0].length;
	}

	//================================================================================================================//
	//                                      Getters and Setters (MISC)
	//================================================================================================================//

	/**
	 * Returns the size of the matrix
	 *
	 * @return The size as a two-element int array -> [rows, columns]
	 */
	public int[] size() {
		return new int[]{matrix.length, matrix[0].length};
	}

	/**
	 * Checks if the matrix is square; i.e. has the same number of rows and columns
	 *
	 * @return True if the matrix is square
	 */
	public boolean isSquare() {
		return matrix.length == matrix[0].length;
	}

	/**
	 * Checks if the matrix is symmetric; i.e. if the matrix is equal to its transpose,
	 * or looks the same after a transpose
	 *
	 * @return True if the matrix is symmetric
	 */
	public boolean isSymmetric() {
		if (!this.isSquare()) {
			return false;
		}
		return this.transpose().equals(this);
	}

	public boolean isSkewSymmetric() {
		if (!this.isSquare()) {
			return false;
		}
		return this.transpose().scale(-1).equals(this);
	}

	public Matrix exchangeColumn(int columnNumber, Matrix otherMatrix) {
		if (columnNumber < 0 || columnNumber >= this.columns()) {
			throw new IllegalArgumentException("Invalid column number");
		}
		if (otherMatrix == null) {
			throw new IllegalArgumentException("No other matrix provided");
		}
		if (otherMatrix.columns() != 1) {
			throw new IllegalArgumentException("Other matrix must be a column vector");
		}
		Matrix matrix = this.copy();

		for (int i = 0; i < matrix.rows(); i++) {
			matrix.matrix[i][columnNumber] = otherMatrix.matrix[i][0];
		}
		return matrix;
	}

	/**
	 * Removes a given row number from the matrix
	 *
	 * @param rowNumber The row number
	 * @return The new matrix without the row
	 */
	public Matrix removeRow(int rowNumber) {
		if (rowNumber < 0 || rowNumber >= this.rows()) {
			throw new IllegalArgumentException("Invalid row number");
		}
		Matrix matrix = new Matrix(this.rows() - 1, this.columns());
		for (int i = 0; i < rowNumber; i++) {
			System.arraycopy(this.matrix[i], 0, matrix.matrix[i], 0, this.columns());
		}
		for (int i = rowNumber + 1; i < this.rows(); i++) {
			System.arraycopy(this.matrix[i], 0, matrix.matrix[i - 1], 0, this.columns());
		}
		return matrix;
	}

	public Matrix removeColumn(int columnNumber) {
		if (columnNumber < 0 || columnNumber >= this.columns()) {
			throw new IllegalArgumentException("Invalid column number");
		}
		return this.transpose().removeRow(columnNumber).transpose();
	}
	//================================================================================================================//
	//                                           Utility Methods
	//================================================================================================================//

	@Override
	public String toString() {
//        return Arrays.deepToString(matrix);

		// Convert values to strings -- find the longest length of all strings
		String[][] stringMatrix = new String[this.rows()][this.columns()];
		int[] columnLongest = new int[this.columns()];
		for (int i = 0; i < this.rows(); i++) {
			for (int j = 0; j < this.columns(); j++) {
				stringMatrix[i][j] = String.valueOf(this.matrix[i][j]);
				if (stringMatrix[i][j].length() > columnLongest[j]) {
					columnLongest[j] = stringMatrix[i][j].length();
				}
			}
		}

		// Pad the strings -- make them all the same length
		// Then append the strings to a single string
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < stringMatrix.length; i++) {
			for (int j = 0; j < stringMatrix[i].length; j++) {
				stringMatrix[i][j] = String.format("%" + columnLongest[j] + "s", stringMatrix[i][j]);
			}
			builder.append(Arrays.toString(stringMatrix[i])).append("\n");
		}

		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Matrix matrix = (Matrix) o;
		return Arrays.deepEquals(this.matrix, matrix.matrix);
	}

	public Matrix copy() {
		return new Matrix(this.matrix);
	}

	public static Matrix copy(Matrix matrix) {
		if (matrix == null) {
			return null;
		}
		return new Matrix(matrix.matrix);
	}
	//================================================================================================================//
	//                                                      Testing
	//================================================================================================================//

	public static void main(String[] args) {
		Matrix matrix1, matrix2;
		Matrix[] result;

		matrix1 = new Matrix(
				new double[][]{
						{1, -1, -1},
						{2, -1, -1},
						{2, 2, 1}
				}
		);
		matrix2 = new Matrix(
				new double[][]{
						{4, 5, -2}
				}
		).transpose();

		result = matrix1.reduce(matrix2);
		System.out.println("Reduce:\n" + result[0] + "==========\n" + result[1]);
		result = matrix1.reducedRowEchelonForm(matrix2);
		System.out.println("Reduced Row Echelon Form:\n" + result[0] + "==========\n" + result[1]);

		System.out.println(matrix1.removeColumn(1));
		System.out.println(matrix1);
		System.out.println(matrix1.removeRow(1));

	}
}
