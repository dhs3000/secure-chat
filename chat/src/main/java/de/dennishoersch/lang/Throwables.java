/*
 * Copyright 2012-2013 Dennis Hörsch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dennishoersch.lang;

/**
 * Helper to throw unchecked exceptions undeclared.
 * 
 * <pre>
 * try {
 *     // do something ...
 * } catch (Exception e) {
 *     throw Throwables.throwUnchecked(e);
 * }
 * </pre>
 * 
 * http://www.gamlor.info/wordpress/2010/02/throwing-checked-excpetions-like-
 * unchecked-exceptions-in-java/
 * 
 * @author hoersch
 */
public final class Throwables {
    private Throwables() {
    }

    /**
     * Throws the given exception as unchecked.
     * 
     * @param e
     * @return nothing because actually the given exception is thrown; But
     *         enables using a 'throw' in the using code
     */
    public static RuntimeException throwUnchecked(final Throwable e) {
	// Now we use the 'generic' method. Normally the type T is inferred
	// from the parameters. However you can specify the type also explicit!
	// Now we du just that! We use the RuntimeException as type!
	// That means the throwsUnchecked throws an unchecked exception!
	// Since the types are erased, no type-information is there to prevent
	// this!
	Throwables.<RuntimeException> throwsUnchecked(e);

	// This is here is only to satisfy the compiler. It's actually
	// unreachable code!
	throw new AssertionError("This code should be unreachable. Something went terrible wrong here!");
    }

    /**
     * Remember, Generics are erased in Java. So this basically throws an
     * Exception. The real Type of T is lost during the compilation
     */
    private static <T extends Throwable> void throwsUnchecked(final Throwable toThrow) throws T {
	// Since the type is erased, this cast actually does nothing!!!
	// we can throw any exception
	@SuppressWarnings("unchecked")
	T t = (T) toThrow;
	throw t;
    }
}
