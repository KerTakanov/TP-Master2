/**
 * @file test_freqs.cxx
 *
 * @author JOL
 */
#include <string>
#include <iostream>
#include <iomanip>
#include "utils.h"
#include "freqs.h"

using namespace std;

int main( int argc, char** argv )
{
  string s = readInput( cin );
  cout << filter_az( s ) << endl;
}
