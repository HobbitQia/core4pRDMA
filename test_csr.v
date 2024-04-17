// To test CSR Module
module test_tb;

    reg clk;
    reg rst;
    wire io_has_event_rd;
    wire [31:0] io_event_recv_cnt;
    wire [31:0] io_event_processed_cnt;
    wire [31:0] io_event_type;
    wire  io_has_event_wr;
    reg  [31:0] io_user_csr_wr_0;
    reg  [31:0] io_user_csr_wr_1;
    reg  [31:0] io_user_csr_wr_31;

    wire [31:0] io_user_csr_rd_0;
    wire [31:0] io_user_csr_rd_1;
    wire [31:0] io_user_csr_rd_31;

    test_csr u0(
      .clock(clk),
      .reset(rst),
      .io_has_event_wr(io_has_event_wr),
      .io_has_event_rd(io_has_event_rd),
      .io_event_recv_cnt(io_event_recv_cnt),
      .io_event_processed_cnt(io_event_processed_cnt),
      .io_event_type(io_event_type),
      .io_user_csr_wr_0(io_user_csr_wr_0),
      .io_user_csr_wr_1(io_user_csr_wr_1),
      .io_user_csr_wr_31(io_user_csr_wr_31),
      .io_user_csr_rd_0(io_user_csr_rd_0),
      .io_user_csr_rd_1(io_user_csr_rd_1),
      .io_user_csr_rd_31(io_user_csr_rd_31)
    );

    assign io_has_event_wr = (io_has_event_rd == 0) && (io_event_recv_cnt == io_event_processed_cnt);

    initial begin
        clk = 0;
        rst = 1;
        io_user_csr_wr_0 = 1;
        io_user_csr_wr_1 = 2;
        io_user_csr_wr_31 = 3;
        #20;
        rst = 0;
        #130;io_user_csr_wr_31 = 5; 
        
    end
    
    always #5 clk = ~clk;
    
endmodule

/*
To test CSR module

test:
    add zero, zero, zero
    csrrw x1, 0x070, zero
    nop
    csrrs x2, 0x071, zero
    csrrs x3, 0x072, zero
    csrrw x1, 0x070, zero
    csrrw zero, 0x072, x2
    csrrs x4, 0x040, zero
    csrrw x5, 0x05F, zero   
    loop: jal loop
    nop
*/