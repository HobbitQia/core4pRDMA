// See LICENSE for license details.

package mini.core

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import mini.junctions._
import common._
import common.storage._
import common.axi._

case class BramParameters( entries : Int, width : Int, staddr : Int, edaddr : Int) {
    require(entries >= 2, "BRAM must has at least 2 entries")
    require(entries <= 1024*1024, "BRAM out of range")
    require(width >= 0, "BRAM's width can't be less than 0")
    require(width <= 1024, "BRAM's width can't be more than 1024")
    require(staddr >= 0, "BRAM's start address can't be less than 0")
    require(edaddr >= 0, "BRAM's end address can't be less than 0")
    require(staddr <= edaddr, "BRAM's start address can't be more than end address")
    // todo
}

class MemArbiterIO(val xlen: Int, params: NastiBundleParameters) extends Bundle {
    val ibram = new CacheIO(xlen, xlen)
    val dbram = new CacheIO(xlen, xlen)
    val nasti = new NastiBundle(params)
}

class MemArbiter(val xlen: Int, val nastiParams: NastiBundleParameters, val bramParams: BramParameters, val cacheParams: CacheConfig) extends Module {
    val io = IO(new MemArbiterIO(xlen, nastiParams))

    val ibram = Module(new Bram(true, bramParams, nastiParams, xlen, "inst.mem"))
    val dbram = Module(new Bram(false, bramParams, nastiParams, xlen, "data.mem"))
    // val dbram2 = Module(new Bram(false, bramParams, nastiParams, xlen, "data2.mem"))
    val cache  = Module(new Cache(cacheParams, nastiParams, xlen))
  
    ibram.io <> io.ibram
    dbram.io.req <> io.dbram.req
    dbram.io.abort := io.dbram.abort
    // dbram2.io.req <> io.dbram.req
    // dbram2.io.abort := io.dbram.abort
    cache.io.cpu.req <> io.dbram.req
    cache.io.cpu.abort := io.dbram.abort
    cache.io.nasti <> io.nasti

    val addr = io.dbram.req.bits.addr
    val range_check = addr >= bramParams.staddr.U && addr < bramParams.edaddr.U
    // val range_reg = RegInit(true.B) 
    val range_reg = RegNext(range_check)
    // when (io.dbram.req.valid) {
    //     range_reg := range_check
    // }

    io.dbram.resp <> Mux(range_reg, dbram.io.resp, cache.io.cpu.resp)

    class ila_arb(seq:Seq[Data]) extends BaseILA(seq)
        val inst_ila_arb = Module(new ila_arb(Seq(				
        reset,
        io.ibram.abort,
        io.dbram.abort,
        range_check,
        range_reg,
        io.ibram.req.valid,
        io.ibram.req.bits.addr,
        io.ibram.resp.bits.data,
        io.dbram.req.bits.addr,
        io.dbram.req.valid,
        io.dbram.req.bits.data,
        io.dbram.req.bits.mask,
        io.dbram.resp.bits.data,
        io.dbram.resp.valid,

        dbram.io.resp.bits.data,
        dbram.io.resp.valid,
        cache.io.cpu.resp.bits.data,
        cache.io.cpu.resp.valid,

        io.nasti.ar.bits.addr,
        io.nasti.ar.valid,
        io.nasti.ar.ready,
        io.nasti.r.bits.data,
        io.nasti.r.valid,
        io.nasti.r.ready,
        io.nasti.aw.bits.addr,
        io.nasti.aw.valid,
        io.nasti.aw.ready,
        io.nasti.w.bits.data,
        io.nasti.w.valid,
        io.nasti.w.ready,
        io.nasti.b.bits.resp,
        io.nasti.b.valid,
        io.nasti.b.ready,

        )))
	  inst_ila_arb.connect(clock)
}

class TileIO(xlen: Int, nastiParams: NastiBundleParameters) extends Bundle {
    val host = new HostIO(xlen)
    val nasti = new NastiBundle(nastiParams)
}

object Tile {
    def apply(config: Config): Tile = new Tile(config.core, config.nasti, config.bram, config.cache)
}


class Tile(val coreParams: CoreConfig, val nastiParams: NastiBundleParameters, val bramParams: BramParameters, val cacheParams: CacheConfig)
    extends Module {
    val io = IO(new TileIO(coreParams.xlen, nastiParams))
    val core = Module(new Core(coreParams))
    val arb = Module(new MemArbiter(coreParams.xlen, nastiParams, bramParams, cacheParams))

    io.host <> core.io.host
    arb.io.ibram <> core.io.icache
    arb.io.dbram <> core.io.dcache
    io.nasti <> arb.io.nasti            // for hbm
}

// 处理 inst
class Bram(val InstBram: Boolean, val b: BramParameters, val nasti: NastiBundleParameters, val xlen: Int, val file: String) extends Module {
    val io = IO(new CacheIO(addrWidth = xlen, dataWidth = xlen))
    
    val bram = XRam(UInt(b.width.W), entries=b.entries, latency=1, use_musk=1, initFile=file)
    if (InstBram) {
        bram.io.addr_a := DontCare
        bram.io.addr_b := io.req.bits.addr(9, 2)
        bram.io.wr_en_a := DontCare
        bram.io.musk_a.get := DontCare    
        bram.io.data_in_a := DontCare
        io.resp.bits.data := bram.io.data_out_b
        io.resp.valid := true.B
    }
    else {
        bram.io.addr_a := io.req.bits.addr(9, 2)
        bram.io.addr_b := DontCare
        bram.io.wr_en_a := io.req.valid && io.req.bits.mask.orR && !io.abort
        bram.io.musk_a.get := io.req.bits.mask.asUInt
        bram.io.data_in_a := io.req.bits.data
        io.resp.bits.data := bram.io.data_out_a
        io.resp.valid := true.B
    }
}
