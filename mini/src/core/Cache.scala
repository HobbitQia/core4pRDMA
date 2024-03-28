// See LICENSE for license details.

package mini.core

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import mini.junctions._
import common._
import common.storage._
import common.axi._

class CacheReq(addrWidth: Int, dataWidth: Int) extends Bundle {
  val addr = UInt(addrWidth.W)
  val data = UInt(dataWidth.W)
  val mask = UInt((dataWidth / 8).W)
}

class CacheResp(dataWidth: Int) extends Bundle {
  val data = UInt(dataWidth.W)
}

class CacheIO(addrWidth: Int, dataWidth: Int) extends Bundle {
  val abort = Input(Bool())
  val req = Flipped(Valid(new CacheReq(addrWidth, dataWidth)))
  val resp = Valid(new CacheResp(dataWidth))
}

class CacheModuleIO(nastiParams: NastiBundleParameters, addrWidth: Int, dataWidth: Int) extends Bundle {
  val cpu = new CacheIO(addrWidth, dataWidth)
  val nasti = new NastiBundle(nastiParams)
}

case class CacheConfig(nWays: Int, nSets: Int, blockBytes: Int)

class MetaData(tagLength: Int) extends Bundle {
  val tag = UInt(tagLength.W)
}

object CacheState extends ChiselEnum {
  val sIdle, sReadCache, sWriteCache, sWriteBack, sWriteAck, sRefillReady, sRefill = Value
}

class Cache(val p: CacheConfig, val nasti: NastiBundleParameters, val xlen: Int) extends Module {
  val io = IO(new CacheModuleIO(nasti, addrWidth = xlen, dataWidth = xlen))

  val wen = io.cpu.req.bits.mask.orR 
  // construct a mask that remote the block offset
  val mem_addr     = io.cpu.req.bits.addr & "h1FFFFFFFC".U
  val addr_reg     = Reg(chiselTypeOf(io.cpu.req.bits.addr))
  val data_reg     = Reg(chiselTypeOf(io.cpu.req.bits.data))
  val valid_reg    = RegNext(io.cpu.resp.valid)
  val resp_data    = Reg(chiselTypeOf(io.cpu.resp.bits.data))

  // val block_offset = io.cpu.req.bits.addr(4, 2) & "h1F".U
  // val mask = "h1FFFFFFFC".U
  // Read
  io.nasti.ar.bits := NastiAddressBundle(nasti)(
    0.U,
    addr_reg,
    2.U,
    0.U
  )
  // io.nasti.ar.valid := io.cpu.req.valid && !wen && !io.cpu.abort
  import CacheState._
  val state = RegInit(sIdle)
  io.nasti.r.ready  := state === sReadCache
  val read_state = (state === sReadCache) && (io.nasti.r.fire === true.B)
  val write_state = (state === sWriteCache) && (io.nasti.b.fire === true.B)
  io.cpu.resp.valid := (state === sIdle) || read_state || write_state
  when(io.cpu.resp.valid) {
    addr_reg := mem_addr
    data_reg := io.cpu.req.bits.data
  }
  when(read_state) {
    resp_data := io.nasti.r.bits.data
  }
  io.cpu.resp.bits.data := Mux(valid_reg, resp_data, io.nasti.r.bits.data)

  // Write
  io.nasti.aw.bits := NastiAddressBundle(nasti)(
    0.U,
    addr_reg,
    2.U,
    0.U
  )
  // io.nasti.aw.valid := io.cpu.req.valid && wen && !io.cpu.abort
  io.nasti.w.valid := state === sWriteCache
  io.nasti.w.bits := NastiWriteDataBundle(nasti)(
    data_reg,
    None,
    true.B
  )
  io.nasti.b.ready := true.B
  io.nasti.ar.valid := false.B
  io.nasti.aw.valid := false.B

  switch(state) {
    is(sIdle) {
      when(io.cpu.abort) {
        state := sIdle
      }.elsewhen(io.cpu.req.valid) {
        state := Mux(wen, sWriteCache, sReadCache)
      }
    }
    is(sReadCache) {
      io.nasti.ar.valid := true.B
      when(io.cpu.abort) {
        state := sIdle
      }.elsewhen(io.nasti.r.fire) {
        when(io.cpu.req.valid) {
          state := Mux(wen, sWriteCache, sReadCache)
        }.otherwise {
          state := sIdle
        }
      }
    }
    is(sWriteCache) {
      io.nasti.aw.valid := true.B
      when(io.cpu.abort) {
        state := sIdle
      }.elsewhen(io.nasti.b.fire) {
        when(io.cpu.req.valid) {
          state := Mux(wen, sWriteCache, sReadCache)
        }.otherwise {
          state := sIdle
        }
      }
    }
  }

  class ila_cache(seq:Seq[Data]) extends BaseILA(seq)
	val inst_ila_cache = Module(new ila_cache(Seq(				
  state,
	io.cpu.req.bits.addr,
  io.cpu.req.bits.data,
  io.cpu.req.bits.mask,
  io.cpu.abort,
  io.cpu.req.valid,
  io.cpu.resp.valid,
  io.cpu.resp.bits.data,

  io.nasti.ar.valid,
  io.nasti.ar.bits.addr,
  io.nasti.ar.ready,
  io.nasti.r.valid,
  io.nasti.r.bits.data,
  io.nasti.r.ready,
  io.nasti.aw.valid,
  io.nasti.aw.bits.addr,
  io.nasti.aw.ready,
  io.nasti.w.valid,
  io.nasti.w.bits.data,
  io.nasti.w.ready,
  io.nasti.b.valid,

	)))
	inst_ila_cache.connect(clock)
}
