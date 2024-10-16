public class Main {
    public static void main(String[] args) {
        Matrix matrix1, matrix2;
        Matrix[] result;
        matrix1 = new Matrix(
                new double[][]{
                        {1, 3, 2},
                        {2, 1, 1},
                        {4, 2, 3}
                }
        );
        matrix2 = Matrix.identity(matrix1.rows());

        result = matrix1.reducedRowEchelonForm(matrix2);
        System.out.println("Reduced Row Echelon Form:\n" + result[0] + "==========\n" + result[1]);

        System.out.println("Inverse:\n" + matrix1.inverse());
    }


}