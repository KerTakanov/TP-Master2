/**
 * @file test_freqs.cxx
 *
 * @author JOL
 */
#include <string>
#include <iostream>
#include <iomanip>
#include "freqs.h"

using namespace std;

int main( int argc, char** argv )
{
  if ( initFreqs( "digrammes.txt" ) != 0 )
    {
      cerr << "[main] problem with initFreqs." << endl;
      return 1;
    }

  cout << setprecision(2);
  for ( int i = 0; i < 26; i++ )
    cout << (char)('A'+i) << " = " << 100*freqN( i ) << endl;
  for ( int i = 0; i < 26; i++ )
    {
      for ( int j = 0; j < 26; j++ )
	cout << " " << (char)('A'+i) << (char)('A'+j) 
	     << " = " << 100*freqMN( i,j );
      cout << endl;
    }

  return 0;
}
