import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
//        Matrix matrix1, matrix2, matrix3, matrix4;
//        Matrix result;
//
//        matrix1 = new Matrix(1, 1);
//        matrix2 = new Matrix(1, 1);
//        matrix3 = new Matrix(1, 1);
//        matrix4 = new Matrix(1, 1);
//        result = new Matrix(1, 1);
//
//
//        matrix1 = new Matrix(
//                new double[][]{
//                        {1, 3},
//                        {1, -4}
//
//                }
//        );
////        matrix2 = Matrix.identity(matrix1.rows());
//        matrix2 = new Matrix(
//                new double[][]{
//                        {390625, 0},
//                        {0, 256}
//                }
//        );
//
//        matrix3 = new Matrix(
//                new double[][]{
//                        {4, 3},
//                        {1, -1}
//                }
//        );
//
//
//        result = matrix1.multiply(matrix3);
//		result.simplify();
//
//        // Keep this
//        System.out.println(result);

		BigFraction[][] input = new BigFraction[][]{
				{new BigFraction(-3), new BigFraction(-33, 8)},
				{new BigFraction(-1), new BigFraction(17, 8)},
				{new BigFraction(0), new BigFraction(3)},
				{new BigFraction(1), new BigFraction(27, 8)},
				{new BigFraction(2), new BigFraction(4)},
				{new BigFraction(3), new BigFraction(45, 8)},
				{new BigFraction(4), new BigFraction(9)}
		};

		BigFraction[] output = Solver.findPolynomial(input);

		System.out.println(Arrays.toString(output));
		System.out.println(toDesmosLatex(output, "x", "g"));
	}

	public static String toDesmosLatex(BigFraction[] coeffs, String varName, String funcName) {
		StringBuilder sb = new StringBuilder();

		// Format function name with subscripts if longer than 1 char
		sb.append(formatWithSubscript(funcName));
		sb.append("\\left(").append(formatWithSubscript(varName)).append("\\right)=");

		boolean first = true;
		for (int i = 0; i < coeffs.length; i++) {
			BigFraction c = coeffs[i];
			if (c.equals(BigFraction.ZERO)) continue;

			long num = c.numerator;
			long den = c.denominator;

			// Sign
			if (!first) {
				sb.append(num >= 0 ? "+" : "-");
			}
			first = false;

			// Absolute value
			num = Math.abs(num);

			if (i == 0) {
				// Constant term
				if (den == 1) {
					sb.append(num);
				} else {
					sb.append("\\frac{").append(num).append("}{").append(den).append("}");
				}
			} else {
				// Coefficient
				if (!(num == 1 && den == 1)) {
					if (den == 1) {
						sb.append(num);
					} else {
						sb.append("\\frac{").append(num).append("}{").append(den).append("}");
					}
				}
				// Variable
				sb.append(formatWithSubscript(varName));
				if (i > 1) sb.append("^").append(i);
			}
		}
		return sb.toString();
	}

	// Helper to turn names into "x_{rest}" form
	private static String formatWithSubscript(String name) {
		if (name.length() == 1) {
			return name;
		} else {
			return name.charAt(0) + "_{" + name.substring(1) + "}";
		}
	}


}