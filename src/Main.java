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


//        result = matrix1.multiply(matrix2).multiply(matrix3);
//
//        // Keep this
//        System.out.println(result);

		double[][] input = new double[][]{
				{0, 0},
				{2, 2},
				{7, 3},
				{12, 4},
				{20, 0}
		};

		double[] output = Solver.findPolynomial(input);

		System.out.println(Arrays.toString(output));
	}


}