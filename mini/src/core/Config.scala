// See LICENSE for license details.

package mini.core

import mini.junctions.NastiBundleParameters

case class Config(core: CoreConfig, cache: CacheConfig, nasti: NastiBundleParameters)

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
        blockBytes = 4
      ),
      nasti = NastiBundleParameters(
        addrBits = 33,
        dataBits = 32,
        idBits = 6
      )
    )
  }
}
