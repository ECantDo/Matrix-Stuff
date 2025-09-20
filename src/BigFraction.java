import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BigFraction uses two integers to make a fraction
 */
public class BigFraction {

	public static final ArrayList<Long> PRIMES = new ArrayList<>(List.of(2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L));

	long numerator, denominator;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public BigFraction() {
		this(0, 1);
	}

	public BigFraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;

		this.updateNegative();
	}

	public BigFraction(long value) {
		this.numerator = value;
		this.denominator = 1;
	}

	public BigFraction(int value) {
		this.numerator = value;
		this.denominator = 1;
	}

	public BigFraction(BigFraction fraction) {
		this(fraction.numerator, fraction.denominator);
	}

	public BigFraction(double value) {
		// NaN
		if (Double.isNaN(value)) {
			this.numerator = 0;
			this.denominator = 0;
			return;
		}

		// INF
		if (Double.isInfinite(value)) {
			this.denominator = 0;
			if (value > 0) {
				this.numerator = 1;
			} else {
				this.numerator = -1;
			}
		}

		// Convert
		long size = (long) Math.pow(10, Double.toString(value).length() - 1);
		if (size == Long.MAX_VALUE) {
			throw new IndexOutOfBoundsException("Size of the fraction is out of bounds.  Number of decimals" +
					" with the conversion to make the fraction, exceeds the integer limit");
		}
		this.numerator = (long) (value * size);
		this.denominator = size;
		this.reduceFraction();
	}

	public static BigFraction valueOf(double value) {
		return new BigFraction(value);
	}

	//==================================================================================================================
	// Static Constructors
	//==================================================================================================================
	public static final BigFraction ZERO = new BigFraction();

	public static final BigFraction ONE = new BigFraction(1);

	//==================================================================================================================
	// Modify
	//==================================================================================================================
	// Easy functions
	public BigFraction multiply(long value) {
		BigFraction f = new BigFraction(this);
		f.numerator *= value;
		return f;
	}

	public BigFraction mult(long value) {
		return this.multiply(value);
	}

	public BigFraction multiply(BigFraction fraction) {
		BigFraction f = new BigFraction(this);
		f.numerator *= fraction.numerator;
		f.denominator *= fraction.denominator;
		return f;
	}

	public BigFraction mult(BigFraction fraction) {
		return this.multiply(fraction);
	}

	/**
	 * Divides this {@link BigFraction} by an integer
	 *
	 * @param value
	 * @return
	 */
	public BigFraction divide(long value) {
		BigFraction f = new BigFraction(this);
		f.denominator *= value;
		return f;
	}

	public BigFraction div(long value) {
		return this.divide(value);
	}

	/**
	 * Divides {@link BigFraction}s, this one from another.
	 *
	 * @param fraction The {@link BigFraction} to divide.
	 * @return Returns the resulting fraction.
	 */
	public BigFraction divide(BigFraction fraction) {
		BigFraction f = new BigFraction(this);
		f.numerator *= fraction.denominator;
		f.denominator *= fraction.numerator;
		return f;
	}

	/**
	 * Abbreviated name for the {@link #divide(BigFraction)} method.<br>
	 * Divides {@link BigFraction}s, this one from another.
	 *
	 * @param fraction The {@link BigFraction} to divide.
	 * @return Returns the resulting fraction.
	 */
	public BigFraction div(BigFraction fraction) {
		return this.divide(fraction);
	}

	// Harder

	/**
	 * Adds two {@link BigFraction} values from each other.  Gives the result in the lowest terms.
	 * Unlike the subtract function, add does not have an abbreviated method for a shorter name.
	 *
	 * @param fraction The fraction to add.
	 * @return Returns a new {@link BigFraction} of the resulting fraction.
	 */
	public BigFraction add(BigFraction fraction) {
		BigFraction f = new BigFraction();
		f.denominator = this.denominator * fraction.denominator;
		f.numerator = this.numerator * fraction.denominator;
		f.numerator += this.denominator * fraction.numerator;
		return f;
	}

	/**
	 * Subtracts two {@link BigFraction} values from each other.  Gives the result in the lowest terms.
	 *
	 * @param fraction The fraction to subtract.
	 * @return Returns a new {@link BigFraction} of the resulting fraction.
	 */
	public BigFraction subtract(BigFraction fraction) {
		BigFraction f = new BigFraction();
		f.denominator = this.denominator * fraction.denominator;
		f.numerator = this.numerator * fraction.denominator;
		f.numerator -= this.denominator * fraction.numerator;
		return f;
	}

	/**
	 * Abbreviated method name for {@link #subtract(BigFraction)}.<br>
	 * See the above method for more details -- subtracts two {@link BigFraction} values.
	 *
	 * @param fraction {@link BigFraction} to subtract.
	 * @return Returns the resulting {@link BigFraction}.
	 */
	public BigFraction sub(BigFraction fraction) {
		return this.subtract(fraction);
	}

	public BigFraction negate() {
		return new BigFraction(-this.numerator, this.denominator);
	}

	// Extras

	/**
	 * Makes a new fraction with the numerator and denominator inverted.
	 *
	 * @return Returns a new {@link BigFraction} with the numerator and denominator swapped.
	 */
	public BigFraction inverse() {
		BigFraction f = new BigFraction(this.denominator, this.numerator);
		return f;
	}

	/**
	 * Makes the sign value of the numerator align with the overall sign of the fraction.
	 * Does not return a new fraction, just updates the current one.
	 */
	public void updateNegative() {
		long count = 0;
		if (this.numerator < 0) {
			this.numerator = -this.numerator;
			count++;
		}
		if (this.denominator < 0) {
			this.denominator = -this.denominator;
			count++;
		}

		this.numerator = count == 1 ? -this.numerator : this.numerator;
	}

/**
 * Gets the fraction in the lowest terms. Returns a new fraction.
 *
 * @return A new {@link BigFraction}, with the same value, in the lowest terms.
 * For example, 10/5 reduces to 2/1; 4/10 reduces to 2/5.
 */
public BigFraction reduce() {
    if (numerator == 0) {
        return new BigFraction(0, 1); // normalize 0/x
    }

    long g = greatestCommonFactor(numerator, denominator);
    long newNum = numerator / g;
    long newDen = denominator / g;

    // normalize so denominator is always positive
    if (newDen < 0) {
        newNum = -newNum;
        newDen = -newDen;
    }

    return new BigFraction(newNum, newDen);
}

	/**
	 * Gets the fraction in the lowest terms.  Updates the values, for internal class use only.
	 */
	private void reduceFraction() {
		if (numerator == 0) {
			denominator = 1; // normalize 0/x
			return;
		}
		long g = greatestCommonFactor(numerator, denominator);
		numerator /= g;
		denominator /= g;

		if (denominator < 0) { // keep denominator positive
			numerator = -numerator;
			denominator = -denominator;
		}
	}

	private static long greatestCommonFactor(long a, long b) {
		a = Math.abs(a);
		b = Math.abs(b);
		while (b != 0) {
			long temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	private static long[] primeFactorize(long number) {
//		System.out.println("Factorizing " + number);
		number = number < 0 ? -number : number;
		long prime = 2;
		ArrayList<Long> primes = new ArrayList<>();
		int idx = 0;
		while (number > 1) {
			double dividedFraction;
			long count = 0;
			do {
				dividedFraction = number / (double) prime;
				if (dividedFraction % 1 != 0) {
					break;
				}
				count++;
				number = (long) dividedFraction;
			} while (true);
			if (count != 0) {
//				System.out.println(number);
			}
			primes.add(count);
			idx++;
			// Get next prime
			if (idx < PRIMES.size()) {
				prime = PRIMES.get(idx);
			} else {
				prime = getNextPrime();
			}
		}
		long[] primeOut = new long[primes.size()];
		idx = 0;
		for (long i : primes) {
			primeOut[idx++] = i;
		}
		return primeOut;
	}

	private static Long getNextPrime() {
//		System.out.print("Getting next prime (");
		// Get next prime
		long prime = PRIMES.getLast();
		boolean foundNewPrime;
		do {
			foundNewPrime = true;
			prime++;
			// Check all previous primes for existing prime
			for (Long i : PRIMES) {
				if (prime % i == 0) {
					foundNewPrime = false;
					break;
				}
			}
		} while (!foundNewPrime);
		PRIMES.add(prime);
//		System.out.println(prime + ")");

		return prime;
	}

	//==================================================================================================================
	// Misc
	//==================================================================================================================
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (!(other instanceof BigFraction)) {
			return false;
		}
		BigFraction fraction = new BigFraction((BigFraction) other).reduce();
		BigFraction copy = new BigFraction(this).reduce();

		return copy.numerator == fraction.numerator && copy.denominator == fraction.denominator;
	}


	//==================================================================================================================
	// Convert
	//==================================================================================================================

	/**
	 * Converts the fraction to a String representation of the fraction.  Does not reduce the fraction.
	 *
	 * @return Returns a String
	 */
	@Override
	public String toString() {
		if (this.denominator == 0) {
			return "NaN";
		}
		BigFraction f = new BigFraction(this);
		f.updateNegative();
		String out = "";
//        out += this.negative ? "-" : "";
		out += f.numerator;
		if (f.denominator != 1) {
			out += "/" + f.denominator;
		}
		return out;
	}

	public double doubleValue() {
		return this.toDouble();
	}

	public double toDouble() {
		if (this.denominator == 0) {
			return Double.NaN;
		}

		return this.numerator / (double) this.denominator;
	}

	public float toFloat() {
		if (this.denominator == 0) {
			return Float.NaN;
		}

		return this.numerator / (float) this.denominator;
	}

	public static void main(String[] args) {
		BigFraction f1 = new BigFraction(-5, 2);
		System.out.println(f1);

		BigFraction f2 = new BigFraction(7, 4);
		System.out.println(f2);

		BigFraction a = f1.add(f2).reduce();
		BigFraction s = f1.sub(f2).reduce();
		BigFraction m = f1.mult(f2).reduce();
		BigFraction d = f1.div(f2).reduce();

		System.out.println("\nRESULTS:");
		System.out.println("+  " + a);
		System.out.println("-  " + s);
		System.out.println("*  " + m);
		System.out.println("/  " + d);

	}

}
