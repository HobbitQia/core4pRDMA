The main part is in `foo/src`, which connects RISC-V mini core, QDMA and HBM. The top module `foo` is in `Foo.scala`. 

`foo/src/TestCSR.scala` is used for testing the CSR module in the RISC-V mini core, especially whether RDMA hardware interface is correct or not. You can also use `test_csr.v` to see the result.

How to build:

``` shell
mill foo AlveoDynamicTop    # build the project
python3 postElaborating.py foo AlveoDynamicTop -t -p
```