// Fichier tp2-ex1.c
#include <stdio.h>
#include <gmpxx.h>

int main( int argc, char ** argv )
{
    mpz_t a;   // Déclare deux variables grands entiers.
    mpz_t b;
    mpz_init( a ); // Les initialise (nécessaire)
    mpz_init( b );
    gmp_scanf( "%Zd", a ); // Saisie clavier
    gmp_scanf( "%Zd", b );
    mpz_t q;  // Déclare deux autres  variables "grands entiers".
    mpz_t r;
    mpz_init( q );   // Les initialise (nécessaire)
    mpz_init( r );

    // Calcule la division de a par b ( a = q * b + r )
    mpz_fdiv_qr( q, r, a, b );

    // Affiche le résultat.
    gmp_printf( "%Zd = %Zd * %Zd + %Zd \n", a, q, b, r );

    // Libère la mémoire
    mpz_clear( a );
    mpz_clear( b );
    mpz_clear( q );
    mpz_clear( r );
}