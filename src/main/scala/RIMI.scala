package chimi

import chisel3._

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


class RIMI() extends Module {
  val io = IO(new Bundle{
    val currentDomain   = Input(UInt(3.W))
    val instrDomain     = Input(UInt(3.W))
    val dmpConfigDomain = Input(UInt(3.W))
    val out1            = Output(Bool())
    val out2            = Output(Bool())
  })

  val rimi1 = Module(new RIMI1())
  rimi1.io.instrDomain   := io.instrDomain
  rimi1.io.currentDomain := io.currentDomain
  io.out1                := rimi1.io.out

  val rimi2 = Module(new RIMI2())
  rimi2.io.dmpConfigDomain := io.dmpConfigDomain
  rimi2.io.currentDomain   := io.currentDomain
  io.out2                  := rimi2.io.out
}


// The Main object extending App to generate the Verilog code.
object RIMIMain extends App {
  // Generate Verilog
  val verilog1 =
    emitVerilog(new RIMI1(), Array("--target-dir", "generated"))
  // Print the generated Verilog code to the console
  println(verilog1)

  // Generate Verilog
  val verilog2 =
    emitVerilog(new RIMI2(), Array("--target-dir", "generated"))
  // Print the generated Verilog code to the console
  println(verilog1)
}