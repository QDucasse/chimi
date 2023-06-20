## Chimi: RIMI verification blocks in Chisel

*Note: This project is a simple test project to use [Chisel](https://github.com/chipsalliance/chisel)!*

### Dependencies

*Extracted from the Chisel doc:*

We recommend LTS releases Java 8 and Java 11. You can install the JDK as recommended by your operating system, or use the prebuilt binaries from [AdoptOpenJDK](https://adoptopenjdk.net/).

SBT is the most common build tool in the Scala community. You can download it [here](https://www.scala-sbt.org/download.html).  
Mill is another Scala/Java build tool without obscure DSL like SBT. You can download it [here](https://github.com/com-lihaoyi/mill/releases)

### Installation

Simply running `sbt run` should install the dependencies and generate the Verilog implementations of both RIMI verifications in the directory `generated/`. To run tests, `sbt test` launches the two tests and generates their waveform in the directory `test_run_dir/`.

### Verifications

The RIMI isolation model adds a *Domain Memory Protection (DMP)* unit on top of the existing *Physical Memory Protection (PMP)*. It uses duplicated memory access instructions linked to a given domain and additional instructions to change domains. The article can be found [here](https://ieeexplore.ieee.org/document/9256494) and this repository is the first step in an inclusion in the [Rocket CPU](https://github.com/chipsalliance/rocket-chip)

Both verifications are simple as they simply check the equality between:
- the decoded code domain of an instruction and the current domain read from a CSR
- the decoded data domain of an instruction and the current domain read from a CSR

The Chisel code is the following:

```scala
/**
  * Performs the first check for the RIMI isolation model.
  * This checks simply asserts that the decoded instruction
  * is executing in its dedicated domain.
  * Note: This check should happen during the decode stage
  * and interrupt the execution if domains do not match.
  */
class RIMI1 extends Module {
    val io = IO(new Bundle {
        val instrDomain   = Input(UInt(3.W)) // Decoder 
        val currentDomain = Input(UInt(3.W)) // CSR read
        val out = Output(Bool())
    })

    io.out := io.instrDomain === io.currentDomain
}


/**
  * Performs the second check for the RIMI isolation model.
  * This check is performed along with the PMP checks without
  * taking precedence on them. The check here compares the
  * domain affected to the pmp region (in dmpcfg) with the 
  * current domain.
  */
class RIMI2() extends Module {
    val io = IO(new Bundle {
        val currentDomain   = Input(UInt(3.W)) // CSR read
        val dmpConfigDomain = Input(UInt(3.W)) // CSR read (?)
        val out   = Output(Bool())
    })

    io.out := io.currentDomain === io.dmpConfigDomain
}
```

### Rocket Questions

- When in the pipeline are CSRs read/written?
- When in the pipeline are the PMP CSRs read?
- When and where is the PMP region checked?
- What verification cost can be hidden in parallel of a pipeline stage?