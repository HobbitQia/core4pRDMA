test_alu:
    addi x1, zero, -1 # x1=FFFFFFFF
    xori x3, x1, 1 # x3=FFFFFFFE
    add x3, x3, x3 # x3=FFFFFFFC
    add x3, x3, x3 # x3=FFFFFFF8
    add x3, x3, x3 # x3=FFFFFFF0
    add x3, x3, x3 # x3=FFFFFFE0
    add x3, x3, x3 # x3=FFFFFFC0
    xor x20, x3, x1 # x20=0000003F
    add x3, x3, x3 # x3=FFFFFF80
    add x3, x3, x3 # x3=FFFFFF00
    add x3, x3, x3 # x3=FFFFFE00
    add x3, x3, x3 # x3=FFFFFC00
    add x3, x3, x3 # x3=FFFFF800
    add x3, x3, x3 # x3=FFFFF000
    add x3, x3, x3 # x3=FFFFE000
    add x3, x3, x3 # x3=FFFFC000
    add x3, x3, x3 # x3=FFFF8000
    add x3, x3, x3 # x3=FFFF0000
    add x3, x3, x3 # x3=FFFE0000
    add x3, x3, x3 # x3=FFFC0000
    add x3, x3, x3 # x3=FFF80000
    add x3, x3, x3 # x3=FFF00000
    add x3, x3, x3 # x3=FFE00000
    add x3, x3, x3 # x3=FFC00000
    add x3, x3, x3 # x3=FF800000
    add x3, x3, x3 # x3=FF000000
    add x3, x3, x3 # x3=FE000000
    add x3, x3, x3 # x3=FC000000
    add x6, x3, x3 # x6=F8000000
    add x3, x6, x6 # x3=F0000000
    add x4, x3, x3 # x4=E0000000
    add x13, x4, x4 # x13=C0000000
    add x8, x13, x13 # x8=80000000
    ori x26, zero, 1 # x26=00000001
    andi x26, x26, 0xff
    srl x27, x8, x26
    jal zero, storeback
    jal zero, end
load:
    ld x0, 0(x0)
    ld x1, 4(x0)
    ld x2, 8(x0)
    ld x3, 12(x0)
    ld x4, 16(x0)
    ld x5, 20(x0)
    ld x6, 24(x0)
    ld x7, 28(x0)
    ld x8, 32(x0)
    ld x9, 36(x0)
    ld x10, 40(x0)
    ld x11, 44(x0)
    ld x12, 48(x0)
    ld x13, 52(x0)
    ld x14, 56(x0)
    ld x15, 60(x0)
    ld x16, 64(x0)
    ld x17, 68(x0)
    ld x18, 72(x0)
    ld x19, 76(x0)
    ld x20, 80(x0)
    ld x21, 84(x0)
    ld x22, 88(x0)
    ld x23, 92(x0)
    ld x24, 96(x0)
    ld x25, 100(x0)
    ld x26, 104(x0)
    ld x27, 108(x0)
    ld x28, 112(x0)
    ld x29, 116(x0)
    ld x30, 120(x0)
    ld x31, 124(x0)
test_csr:
    csrrw x1, mstatus, x0
    csrrw x2, mtvec, x0
    csrrw x3, mepc, x0
    csrrw x4, mcause, x0
    csrrw x5, mtval, x0
    csrrw x6, mip, x0
    csrrw x7, mie, x0
    csrrs x2, mtvec, x1
    csrrs x3, mepc, x2
    csrrw x2, mtvec, x3
    csrrw x3, mepc, x4

storeback:
    sw x0, 0(x0)
    sw x1, 4(x0)
    sw x2, 8(x0)
    sw x3, 12(x0)
    sw x4, 16(x0)
    sw x5, 20(x0)
    sw x6, 24(x0)
    sw x7, 28(x0)
    sw x8, 32(x0)
    sw x9, 36(x0)
    sw x10, 40(x0)
    sw x11, 44(x0)
    sw x12, 48(x0)
    sw x13, 52(x0)
    sw x14, 56(x0)
    sw x15, 60(x0)
    sw x16, 64(x0)
    sw x17, 68(x0)
    sw x18, 72(x0)
    sw x19, 76(x0)
    sw x20, 80(x0)
    sw x21, 84(x0)
    sw x22, 88(x0)
    sw x23, 92(x0)
    sw x24, 96(x0)
    sw x25, 100(x0)
    sw x26, 104(x0)
    sw x27, 108(x0)
    sw x28, 112(x0)
    sw x29, 116(x0)
    sw x30, 120(x0)
    sw x31, 124(x0)

    li x1, 0b10000000000000
    
    sw x1, 0(x1)
    sw x1, 32(x1)
    sw x1, 64(x1)
    sw x1, 96(x1)
    sw x1, 128(x1)
    add zero, zero, zero

end:
    beq x0, x0, end
    add x0, x0, x0
/*
0xFFF00093
0x0010C193
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x0011CA33
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x003181B3
0x00318333
0x006301B3
0x00318233
0x004206B3
0x00D68433
0x00106D13
0x0FFD7D13
0x01A45DB3
0x0880006F
0x1040006F
0x00003003
0x00403083
0x00803103
0x00C03183
0x01003203
0x01403283
0x01803303
0x01C03383
0x02003403
0x02403483
0x02803503
0x02C03583
0x03003603
0x03403683
0x03803703
0x03C03783
0x04003803
0x04403883
0x04803903
0x04C03983
0x05003A03
0x05403A83
0x05803B03
0x05C03B83
0x06003C03
0x06403C83
0x06803D03
0x06C03D83
0x07003E03
0x07403E83
0x07803F03
0x07C03F83
0x00002023
0x00102223
0x00202423
0x00302623
0x00402823
0x00502A23
0x00602C23
0x00702E23
0x02802023
0x02902223
0x02A02423
0x02B02623
0x02C02823
0x02D02A23
0x02E02C23
0x02F02E23
0x05002023
0x05102223
0x05202423
0x05302623
0x05402823
0x05502A23
0x05602C23
0x05702E23
0x07802023
0x07902223
0x07A02423
0x07B02623
0x07C02823
0x07D02A23
0x07E02C23
0x07F02E23
0x00000063
0x00000033
*/

00102c2300102a230010282300102623001024230010222300102023fff00093
000000000000020d000000000000003300000033000000330000006300000033

200 0xFFF00093
204 0x00102023
208 0x00102223
20C 0x00102423
210 0x00102623
214 0x00102823
218 0x00102A23
21C 0x00102C23
220 0x00000033
224 0x00000063
228 0x00000033
22C 0x00000033

0xFFF00093
0x00102023
0x00102223
0x00102423
0x00102623
0x00102823
0x00102A23
0x00102C23
0x00002023
0x00000033
0x00000063
0x00000033
0x00000033
