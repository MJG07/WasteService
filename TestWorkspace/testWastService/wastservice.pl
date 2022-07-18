%====================================================================================
% wastservice description   
%====================================================================================
context(ctxwastservice, "localhost",  "TCP", "8032").
 qactor( wastservice, ctxwastservice, "it.unibo.wastservice.Wastservice").
  qactor( transporttrolley, ctxwastservice, "it.unibo.transporttrolley.Transporttrolley").
