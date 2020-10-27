HamCyc_z3.py:

  solving Hamiltonian cycle with the standard Microsoft's
  encoding from
  https://github.com/Z3Prover/z3/blob/master/examples/python/hamiltonian/hamiltonian.py

hamiltonian_cycle_igraph.py:

  solving Hamiltonian cycle with the subgraph isomorphism function
  from igraph (find a cycle of length == number of vertices). Slow.

test3colorability.py:

  Testing if a graph is 3-colourable with by a straightforward
  encoding for pycosat. Surprisingly fast.


DomSet_pycosat.py:

  Testing if a graph has a dominating set of size k. The number k can
  be input from Gralog. Extremely slow for k>4.
