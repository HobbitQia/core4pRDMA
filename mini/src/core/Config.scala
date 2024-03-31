// See LICENSE for license details.

package mini.core

import mini.junctions.NastiBundleParameters
import mini.core.BramParameters

case class Config(core: CoreConfig, cache: CacheConfig, bram: BramParameters, nasti: NastiBundleParameters)

object MiniConfig {
  def apply(): Config = {
    val xlen = 32
    Config(
      core = CoreConfig(
        xlen = xlen,
        makeAlu = new AluArea(_),
        makeBrCond = new BrCondArea(_),
        makeImmGen = new ImmGenWire(_)
      ),
      cache = CacheConfig(
        nWays = 1,
        nSets = 256,
        blockBytes = 32
      ),
      bram = BramParameters(
        entries = 256,
        width = 32,
        staddr = 0x0,
        edaddr = 0x400
      ),
      nasti = NastiBundleParameters(
        addrBits = 33,
        dataBits = 256,
        idBits = 6
      )
    )
  }
}
