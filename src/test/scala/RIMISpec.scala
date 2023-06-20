package chimi.tests

import chimi._

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class RIMI1Test extends AnyFlatSpec with ChiselScalatestTester {
  "RIMI1" should "perform the first verification" in {
    test(new RIMI1).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for (curDom <- 0 until 2) {
        for (instrDom <- 0 until 2) {
            dut.io.currentDomain.poke(curDom)
            dut.io.instrDomain.poke(instrDom)
            dut.clock.step()
            assert(dut.io.out.peekBoolean() === (curDom === instrDom))
        }
      }
    }
  }
}


class RIMI2Test extends AnyFlatSpec with ChiselScalatestTester {
  "RIMI2" should "perform the second verification" in {
    test(new RIMI2).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for (curDom <- 0 until 2) {
        for (dmpDom <- 0 until 2) {
            dut.io.currentDomain.poke(curDom)
            dut.io.dmpConfigDomain.poke(dmpDom)
            dut.clock.step()
            assert(dut.io.out.peekBoolean() === (curDom === dmpDom))
        }
      }
    }
  }
}
