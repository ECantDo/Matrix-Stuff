

public class Solver {
	/**
	 * Find the polynomial of best fit for a series of inputs
	 *
	 * @param inputs Array of pairs of inputs: [ [x, y], [x1, y1], [x2, y2], ... ]
	 * @return Each index is the x^n constant value for each x that has a solution up to the maximum x^n.
	 * Returns null if the input is incorrect, or it cannot be solved.
	 */
	public static BigFraction[] findPolynomial(double[][] inputs) {

		Matrix vandermondeMatrix = new Matrix(inputs.length);
		Matrix yMatrix = new Matrix(inputs.length, 1);

		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].length != 2) return null;
			double x = inputs[i][0];
			yMatrix.set(i, 0, inputs[i][1]);
			for (int j = 0; j < inputs.length; j++) {
				double pow = j == 0 ? 1 : Math.pow(x, j);

				vandermondeMatrix.set(i, j, pow);
			}
		}

		Matrix[] solution = vandermondeMatrix.reducedRowEchelonForm(yMatrix);

		BigFraction[] result = new BigFraction[yMatrix.rows()];
		for (int i = 0; i < result.length; i++) {
			result[i] = solution[1].get(i, 0);
		}

//		for (Matrix m : solution){
//			System.out.println(m.toString());
//		}

		return result;
	}
}
