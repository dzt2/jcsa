	Label	Killed	Alive	Type	Location	Words	Line	Code
@Pattern	Unknown	0	0	#expr	ArithExpression[428]	[]	596	"a % b"
@Pattern	Unknown	0	0	#cons	IfStatement[418]	[]	594	"while(b != 0) {         t = b;         b = a % b;         a = t;     }"
@Pattern	Unknown	0	0	#cons	IfStatement[644]	[]	647	"if(argc > 3)     {         x = atoi(argv[1]);         y = atoi(argv[2]);         z = atoi(argv[3]);         if(is_primitive(x, y, z) && is_hereonal(x, y, z))             printf("Yes!\n");         else          printf("No!\n");     }"
@Pattern	Unknown	0	0	#cons	CallStatement[465]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[429]	[]	596	"b = a % b"
@Pattern	Unknown	0	0	#stat	Identifier[425]	[]	596	"b"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[429]	[]	596	"b = a % b"
@Pattern	Unknown	0	0	#cons	CallStatement[697]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#expr	RelationExpression[417]	[]	594	"b != 0"
@Pattern	Unknown	0	0	#trap	IfStatement[418]	[]	594	"while(b != 0) {         t = b;         b = a % b;         a = t;     }"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[440]	[]	599	"return a;"
@Pattern	Unknown	0	0	#refr	Identifier[438]	[]	599	"a"
@Pattern	Unknown	0	0	#stat	ReturnPoint[439]	[]	599	"return"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[440]	[]	599	"return a;"
@Pattern	Unknown	0	0	#refr	Identifier[415]	[]	594	"b"
@Pattern	Unknown	0	0	#stat	Identifier[421]	[]	595	"t"
@Pattern	Unknown	0	0	#refr	Identifier[422]	[]	595	"b"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[423]	[]	595	"t = b"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[423]	[]	595	"t = b"
@Pattern	Unknown	0	0	#stat	Identifier[431]	[]	597	"a"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[433]	[]	597	"a = t"
@Pattern	Unknown	0	0	#refr	Identifier[432]	[]	597	"t"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[433]	[]	597	"a = t"
@Pattern	Unknown	0	0	#refr	Identifier[426]	[]	596	"a"
@Pattern	Unknown	0	0	#refr	Identifier[427]	[]	596	"b"
@Pattern	Unknown	0	0	#expr	Identifier[438]	[]	599	"a"
@Pattern	Unknown	0	0	#stat	Identifier[438]	[]	599	"a"
@Pattern	Unknown	0	0	#stat	Identifier[415]	[]	594	"b"
@Pattern	Unknown	0	0	#expr	Identifier[415]	[]	594	"b"
@Pattern	Unknown	0	0	#stat	Identifier[422]	[]	595	"b"
@Pattern	Unknown	0	0	#expr	Identifier[422]	[]	595	"b"
@Pattern	Unknown	0	0	#expr	Identifier[432]	[]	597	"t"
@Pattern	Unknown	0	0	#stat	Identifier[432]	[]	597	"t"
@Pattern	Unknown	0	0	#stat	Identifier[426]	[]	596	"a"
@Pattern	Unknown	0	0	#expr	Identifier[426]	[]	596	"a"
@Pattern	Unknown	0	0	#expr	Identifier[427]	[]	596	"b"
@Pattern	Unknown	0	0	#stat	Identifier[427]	[]	596	"b"
@Pattern	Unknown	0	0	#flow	InitAssignStatement[414]	[]	593	"t"
@Pattern	Unknown	0	0	#cons	InitAssignStatement[414]	[]	593	"t"
@Pattern	Unknown	0	0	#flow	IfStatement[418]	[]	594	"while(b != 0) {         t = b;         b = a % b;         a = t;     }"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[423]	[]	595	"t = b"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[429]	[]	596	"b = a % b"
@Pattern	Unknown	0	0	#expr	ConstExpression[416]	[]	594	"0"
@Pattern	Unknown	0	0	#trap	InitAssignStatement[414]	[]	593	"t"
@Pattern	Unknown	0	0	#expr	RelationExpression[483]	[]	609	"(c % t) == 0"
@Pattern	Unknown	0	0	#cons	IfStatement[484]	[]	609	"if((c % t) == 0)             return 0;         else          return 1;"
@Pattern	Unknown	0	0	#cons	IfStatement[476]	[]	607	"if(t > 1)     {         if((c % t) == 0)             return 0;         else          return 1;     }"
@Pattern	Unknown	0	0	#expr	ArithExpression[481]	[]	609	"c % t"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[469]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#trap	IfStatement[484]	[]	609	"if((c % t) == 0)             return 0;         else          return 1;"
@Pattern	Unknown	0	0	#expr	RelationExpression[475]	[]	607	"t > 1"
@Pattern	Unknown	0	0	#trap	IfStatement[476]	[]	607	"if(t > 1)     {         if((c % t) == 0)             return 0;         else          return 1;     }"
@Pattern	Unknown	0	0	#refr	Identifier[473]	[]	607	"t"
@Pattern	Unknown	0	0	#stat	Implicator[468]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#refr	Identifier[463]	[]	606	"a"
@Pattern	Unknown	0	0	#expr	WaitExpression[467]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#trap	CallStatement[465]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#refr	Identifier[464]	[]	606	"b"
@Pattern	Unknown	0	0	#refr	Identifier[479]	[]	609	"c"
@Pattern	Unknown	0	0	#refr	Identifier[480]	[]	609	"t"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[471]	[]	606	"t = gcd(a, b)"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[471]	[]	606	"t = gcd(a, b)"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[469]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[507]	[]	614	"return 1;"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[507]	[]	614	"return 1;"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[489]	[]	610	"return 0;"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[489]	[]	610	"return 0;"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[496]	[]	612	"return 1;"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[496]	[]	612	"return 1;"
@Pattern	Unknown	0	0	#expr	Identifier[473]	[]	607	"t"
@Pattern	Unknown	0	0	#stat	Identifier[473]	[]	607	"t"
@Pattern	Unknown	0	0	#expr	Identifier[463]	[]	606	"a"
@Pattern	Unknown	0	0	#stat	Identifier[463]	[]	606	"a"
@Pattern	Unknown	0	0	#stat	Identifier[464]	[]	606	"b"
@Pattern	Unknown	0	0	#expr	Identifier[464]	[]	606	"b"
@Pattern	Unknown	0	0	#expr	Identifier[479]	[]	609	"c"
@Pattern	Unknown	0	0	#stat	Identifier[479]	[]	609	"c"
@Pattern	Unknown	0	0	#stat	Identifier[480]	[]	609	"t"
@Pattern	Unknown	0	0	#expr	Identifier[480]	[]	609	"t"
@Pattern	Unknown	0	0	#stat	Identifier[460]	[]	606	"t"
@Pattern	Unknown	0	0	#expr	Implicator[470]	[]	606	"gcd(a, b)"
@Pattern	Unknown	0	0	#expr	ConstExpression[505]	[]	614	"1"
@Pattern	Unknown	0	0	#stat	ReturnPoint[506]	[]	614	"return"
@Pattern	Unknown	0	0	#expr	ConstExpression[474]	[]	607	"1"
@Pattern	Unknown	0	0	#expr	ConstExpression[482]	[]	609	"0"
@Pattern	Unknown	0	0	#expr	ConstExpression[487]	[]	610	"0"
@Pattern	Unknown	0	0	#stat	ReturnPoint[488]	[]	610	"return"
@Pattern	Unknown	0	0	#stat	ReturnPoint[495]	[]	612	"return"
@Pattern	Unknown	0	0	#expr	ConstExpression[494]	[]	612	"1"
@Pattern	Unknown	0	0	#flow	InitAssignStatement[459]	[]	605	"t"
@Pattern	Unknown	0	0	#cons	InitAssignStatement[459]	[]	605	"t"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[471]	[]	606	"t = gcd(a, b)"
@Pattern	Unknown	0	0	#flow	IfStatement[476]	[]	607	"if(t > 1)     {         if((c % t) == 0)             return 0;         else          return 1;     }"
@Pattern	Unknown	0	0	#trap	InitAssignStatement[459]	[]	605	"t"
@Pattern	Unknown	0	0	#cons	IfStatement[706]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[701]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[542]	[]	621	"sum = a + b + c"
@Pattern	Unknown	0	0	#cons	CallStatement[714]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#expr	ArithExpression[541]	[]	621	"a + b + c"
@Pattern	Unknown	0	0	#stat	Identifier[536]	[]	621	"sum"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[542]	[]	621	"sum = a + b + c"
@Pattern	Unknown	0	0	#expr	ArithExpression[539]	[]	621	"a + b"
@Pattern	Unknown	0	0	#cons	IfStatement[549]	[]	622	"if((sum % 2) != 0)     {         return 0;     }     else     {         s = sum / 2;         area2 = s * (s - a) * (s - b) * (s - c);         if(area2 <= 0)         {             return 0;         }         else         {             area = sqrt(area2);             return area2 == area * area;         }     }"
@Pattern	Unknown	0	0	#expr	ArithExpression[546]	[]	622	"sum % 2"
@Pattern	Unknown	0	0	#expr	RelationExpression[548]	[]	622	"(sum % 2) != 0"
@Pattern	Unknown	0	0	#trap	IfStatement[549]	[]	622	"if((sum % 2) != 0)     {         return 0;     }     else     {         s = sum / 2;         area2 = s * (s - a) * (s - b) * (s - c);         if(area2 <= 0)         {             return 0;         }         else         {             area = sqrt(area2);             return area2 == area * area;         }     }"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[563]	[]	628	"s = sum / 2"
@Pattern	Unknown	0	0	#stat	Identifier[559]	[]	628	"s"
@Pattern	Unknown	0	0	#expr	ArithExpression[562]	[]	628	"sum / 2"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[563]	[]	628	"s = sum / 2"
@Pattern	Unknown	0	0	#expr	ArithExpression[578]	[]	629	"s * (s - a) * (s - b) * (s - c)"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[579]	[]	629	"area2 = s * (s - a) * (s - b) * (s - c)"
@Pattern	Unknown	0	0	#stat	Identifier[565]	[]	629	"area2"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[579]	[]	629	"area2 = s * (s - a) * (s - b) * (s - c)"
@Pattern	Unknown	0	0	#expr	ArithExpression[574]	[]	629	"s * (s - a) * (s - b)"
@Pattern	Unknown	0	0	#expr	ArithExpression[570]	[]	629	"s * (s - a)"
@Pattern	Unknown	0	0	#expr	ArithExpression[577]	[]	629	"s - c"
@Pattern	Unknown	0	0	#expr	ArithExpression[573]	[]	629	"s - b"
@Pattern	Unknown	0	0	#cons	IfStatement[584]	[]	630	"if(area2 <= 0)         {             return 0;         }         else         {             area = sqrt(area2);             return area2 == area * area;         }"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[612]	[]	637	"return area2 == area * area;"
@Pattern	Unknown	0	0	#expr	RelationExpression[610]	[]	637	"area2 == area * area"
@Pattern	Unknown	0	0	#expr	ArithExpression[609]	[]	637	"area * area"
@Pattern	Unknown	0	0	#stat	ReturnPoint[611]	[]	637	"return"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[612]	[]	637	"return area2 == area * area;"
@Pattern	Unknown	0	0	#expr	ArithExpression[569]	[]	629	"s - a"
@Pattern	Unknown	0	0	#expr	RelationExpression[583]	[]	630	"area2 <= 0"
@Pattern	Unknown	0	0	#trap	IfStatement[584]	[]	630	"if(area2 <= 0)         {             return 0;         }         else         {             area = sqrt(area2);             return area2 == area * area;         }"
@Pattern	Unknown	0	0	#refr	Identifier[540]	[]	621	"c"
@Pattern	Unknown	0	0	#refr	Identifier[537]	[]	621	"a"
@Pattern	Unknown	0	0	#refr	Identifier[538]	[]	621	"b"
@Pattern	Unknown	0	0	#refr	Identifier[544]	[]	622	"sum"
@Pattern	Unknown	0	0	#refr	Identifier[581]	[]	630	"area2"
@Pattern	Unknown	0	0	#refr	Identifier[560]	[]	628	"sum"
@Pattern	Unknown	0	0	#refr	Identifier[566]	[]	629	"s"
@Pattern	Unknown	0	0	#refr	Identifier[576]	[]	629	"c"
@Pattern	Unknown	0	0	#refr	Identifier[606]	[]	637	"area2"
@Pattern	Unknown	0	0	#refr	Identifier[572]	[]	629	"b"
@Pattern	Unknown	0	0	#refr	Identifier[607]	[]	637	"area"
@Pattern	Unknown	0	0	#refr	Identifier[568]	[]	629	"a"
@Pattern	Unknown	0	0	#refr	Identifier[597]	[]	636	"area2"
@Pattern	Unknown	0	0	#stat	Implicator[601]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#cons	CallStatement[598]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#expr	WaitExpression[600]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[602]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#trap	CallStatement[598]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[554]	[]	624	"return 0;"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[554]	[]	624	"return 0;"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[589]	[]	632	"return 0;"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[589]	[]	632	"return 0;"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[604]	[]	636	"area = sqrt(area2)"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[604]	[]	636	"area = sqrt(area2)"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[602]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#stat	Identifier[540]	[]	621	"c"
@Pattern	Unknown	0	0	#expr	Identifier[540]	[]	621	"c"
@Pattern	Unknown	0	0	#expr	Identifier[537]	[]	621	"a"
@Pattern	Unknown	0	0	#stat	Identifier[537]	[]	621	"a"
@Pattern	Unknown	0	0	#stat	Identifier[538]	[]	621	"b"
@Pattern	Unknown	0	0	#expr	Identifier[538]	[]	621	"b"
@Pattern	Unknown	0	0	#expr	Identifier[544]	[]	622	"sum"
@Pattern	Unknown	0	0	#stat	Identifier[544]	[]	622	"sum"
@Pattern	Unknown	0	0	#stat	Identifier[581]	[]	630	"area2"
@Pattern	Unknown	0	0	#expr	Identifier[581]	[]	630	"area2"
@Pattern	Unknown	0	0	#expr	Identifier[560]	[]	628	"sum"
@Pattern	Unknown	0	0	#stat	Identifier[560]	[]	628	"sum"
@Pattern	Unknown	0	0	#stat	Identifier[566]	[]	629	"s"
@Pattern	Unknown	0	0	#expr	Identifier[566]	[]	629	"s"
@Pattern	Unknown	0	0	#expr	Identifier[576]	[]	629	"c"
@Pattern	Unknown	0	0	#stat	Identifier[576]	[]	629	"c"
@Pattern	Unknown	0	0	#stat	Identifier[606]	[]	637	"area2"
@Pattern	Unknown	0	0	#expr	Identifier[606]	[]	637	"area2"
@Pattern	Unknown	0	0	#stat	Identifier[572]	[]	629	"b"
@Pattern	Unknown	0	0	#expr	Identifier[572]	[]	629	"b"
@Pattern	Unknown	0	0	#expr	Identifier[607]	[]	637	"area"
@Pattern	Unknown	0	0	#stat	Identifier[607]	[]	637	"area"
@Pattern	Unknown	0	0	#expr	Identifier[568]	[]	629	"a"
@Pattern	Unknown	0	0	#stat	Identifier[568]	[]	629	"a"
@Pattern	Unknown	0	0	#stat	Identifier[597]	[]	636	"area2"
@Pattern	Unknown	0	0	#expr	Identifier[597]	[]	636	"area2"
@Pattern	Unknown	0	0	#expr	ConstExpression[547]	[]	622	"0"
@Pattern	Unknown	0	0	#expr	ConstExpression[545]	[]	622	"2"
@Pattern	Unknown	0	0	#expr	ConstExpression[552]	[]	624	"0"
@Pattern	Unknown	0	0	#stat	ReturnPoint[553]	[]	624	"return"
@Pattern	Unknown	0	0	#expr	ConstExpression[582]	[]	630	"0"
@Pattern	Unknown	0	0	#expr	ConstExpression[561]	[]	628	"2"
@Pattern	Unknown	0	0	#expr	ConstExpression[587]	[]	632	"0"
@Pattern	Unknown	0	0	#stat	ReturnPoint[588]	[]	632	"return"
@Pattern	Unknown	0	0	#cons	InitAssignStatement[535]	[]	620	"area"
@Pattern	Unknown	0	0	#flow	InitAssignStatement[535]	[]	620	"area"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[542]	[]	621	"sum = a + b + c"
@Pattern	Unknown	0	0	#flow	IfStatement[549]	[]	622	"if((sum % 2) != 0)     {         return 0;     }     else     {         s = sum / 2;         area2 = s * (s - a) * (s - b) * (s - c);         if(area2 <= 0)         {             return 0;         }         else         {             area = sqrt(area2);             return area2 == area * area;         }     }"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[563]	[]	628	"s = sum / 2"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[579]	[]	629	"area2 = s * (s - a) * (s - b) * (s - c)"
@Pattern	Unknown	0	0	#flow	IfStatement[584]	[]	630	"if(area2 <= 0)         {             return 0;         }         else         {             area = sqrt(area2);             return area2 == area * area;         }"
@Pattern	Unknown	0	0	#stat	Identifier[594]	[]	636	"area"
@Pattern	Unknown	0	0	#expr	Implicator[603]	[]	636	"sqrt(area2)"
@Pattern	Unknown	0	0	#cons	InitAssignStatement[526]	[]	620	"sum"
@Pattern	Unknown	0	0	#trap	InitAssignStatement[526]	[]	620	"sum"
@Pattern	Unknown	0	0	#expr	RelationExpression[643]	[]	647	"argc > 3"
@Pattern	Unknown	0	0	#trap	IfStatement[644]	[]	647	"if(argc > 3)     {         x = atoi(argv[1]);         y = atoi(argv[2]);         z = atoi(argv[3]);         if(is_primitive(x, y, z) && is_hereonal(x, y, z))             printf("Yes!\n");         else          printf("No!\n");     }"
@Pattern	Unknown	0	0	#refr	Identifier[641]	[]	647	"argc"
@Pattern	Unknown	0	0	#stat	Implicator[700]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#refr	Identifier[694]	[]	652	"x"
@Pattern	Unknown	0	0	#expr	WaitExpression[699]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#trap	CallStatement[697]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#refr	Identifier[695]	[]	652	"y"
@Pattern	Unknown	0	0	#refr	Identifier[696]	[]	652	"z"
@Pattern	Unknown	0	0	#expr	WaitExpression[716]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[718]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#refr	Identifier[711]	[]	652	"x"
@Pattern	Unknown	0	0	#stat	Implicator[717]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#trap	CallStatement[714]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#refr	Identifier[712]	[]	652	"y"
@Pattern	Unknown	0	0	#refr	Identifier[713]	[]	652	"z"
@Pattern	Unknown	0	0	#trap	ReturnAssignStatement[755]	[]	657	"return 0;"
@Pattern	Unknown	0	0	#cons	ReturnAssignStatement[755]	[]	657	"return 0;"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[660]	[]	649	"x = atoi(argv[1])"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[660]	[]	649	"x = atoi(argv[1])"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[658]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[658]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[675]	[]	650	"y = atoi(argv[2])"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[675]	[]	650	"y = atoi(argv[2])"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[673]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[673]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#trap	BinAssignStatement[690]	[]	651	"z = atoi(argv[3])"
@Pattern	Unknown	0	0	#cons	BinAssignStatement[690]	[]	651	"z = atoi(argv[3])"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[688]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[688]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#trap	IfEndStatement[722]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#cons	IfEndStatement[722]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[701]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[718]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#cons	IfStatement[724]	[]	652	"if(is_primitive(x, y, z) && is_hereonal(x, y, z))             printf("Yes!\n");         else          printf("No!\n");"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[734]	[]	653	"printf("Yes!\n")"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[734]	[]	653	"printf("Yes!\n")"
@Pattern	Unknown	0	0	#cons	WaitAssignStatement[745]	[]	655	"printf("No!\n")"
@Pattern	Unknown	0	0	#trap	WaitAssignStatement[745]	[]	655	"printf("No!\n")"
@Pattern	Unknown	0	0	#cons	CallStatement[654]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#trap	CallStatement[654]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#trap	CallStatement[669]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#cons	CallStatement[669]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#cons	CallStatement[684]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#trap	CallStatement[684]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#stat	Identifier[641]	[]	647	"argc"
@Pattern	Unknown	0	0	#expr	Identifier[641]	[]	647	"argc"
@Pattern	Unknown	0	0	#stat	Identifier[694]	[]	652	"x"
@Pattern	Unknown	0	0	#expr	Identifier[694]	[]	652	"x"
@Pattern	Unknown	0	0	#stat	Identifier[695]	[]	652	"y"
@Pattern	Unknown	0	0	#expr	Identifier[695]	[]	652	"y"
@Pattern	Unknown	0	0	#stat	Identifier[696]	[]	652	"z"
@Pattern	Unknown	0	0	#expr	Identifier[696]	[]	652	"z"
@Pattern	Unknown	0	0	#stat	Identifier[711]	[]	652	"x"
@Pattern	Unknown	0	0	#expr	Identifier[711]	[]	652	"x"
@Pattern	Unknown	0	0	#stat	Identifier[712]	[]	652	"y"
@Pattern	Unknown	0	0	#expr	Identifier[712]	[]	652	"y"
@Pattern	Unknown	0	0	#expr	Identifier[713]	[]	652	"z"
@Pattern	Unknown	0	0	#stat	Identifier[713]	[]	652	"z"
@Pattern	Unknown	0	0	#stat	Identifier[647]	[]	649	"x"
@Pattern	Unknown	0	0	#expr	Implicator[659]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#expr	Implicator[674]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#stat	Identifier[662]	[]	650	"y"
@Pattern	Unknown	0	0	#stat	Identifier[677]	[]	651	"z"
@Pattern	Unknown	0	0	#expr	Implicator[689]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#expr	Implicator[723]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#expr	ConstExpression[753]	[]	657	"0"
@Pattern	Unknown	0	0	#stat	ReturnPoint[754]	[]	657	"return"
@Pattern	Unknown	0	0	#expr	ConstExpression[642]	[]	647	"3"
@Pattern	Unknown	0	0	#expr	ArithExpression[652]	[]
@Pattern	Unknown	0	0	#stat	Implicator[657]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#expr	ConstExpression[651]	[]	649	"1"
@Pattern	Unknown	0	0	#expr	WaitExpression[656]	[]	649	"atoi(argv[1])"
@Pattern	Unknown	0	0	#refr	DeferExpression[653]	[]	649	"argv[1]"
@Pattern	Unknown	0	0	#expr	ArithExpression[667]	[]
@Pattern	Unknown	0	0	#refr	DeferExpression[668]	[]	650	"argv[2]"
@Pattern	Unknown	0	0	#expr	ConstExpression[666]	[]	650	"2"
@Pattern	Unknown	0	0	#expr	WaitExpression[671]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#stat	Implicator[672]	[]	650	"atoi(argv[2])"
@Pattern	Unknown	0	0	#expr	ConstExpression[681]	[]	651	"3"
@Pattern	Unknown	0	0	#expr	ArithExpression[682]	[]
@Pattern	Unknown	0	0	#stat	Implicator[687]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#expr	WaitExpression[686]	[]	651	"atoi(argv[3])"
@Pattern	Unknown	0	0	#refr	DeferExpression[683]	[]	651	"argv[3]"
@Pattern	Unknown	0	0	#cons	InitAssignStatement[640]	[]	646	"z"
@Pattern	Unknown	0	0	#flow	InitAssignStatement[640]	[]	646	"z"
@Pattern	Unknown	0	0	#flow	IfStatement[644]	[]	647	"if(argc > 3)     {         x = atoi(argv[1]);         y = atoi(argv[2]);         z = atoi(argv[3]);         if(is_primitive(x, y, z) && is_hereonal(x, y, z))             printf("Yes!\n");         else          printf("No!\n");     }"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[660]	[]	649	"x = atoi(argv[1])"
@Pattern	Unknown	0	0	#flow	BinAssignStatement[675]	[]	650	"y = atoi(argv[2])"
@Pattern	Unknown	0	0	#flow	IfEndStatement[722]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#flow	IfStatement[724]	[]	652	"if(is_primitive(x, y, z) && is_hereonal(x, y, z))             printf("Yes!\n");         else          printf("No!\n");"
@Pattern	Unknown	0	0	#cons	CallStatement[730]	[]	653	"printf("Yes!\n")"
@Pattern	Unknown	0	0	#cons	CallStatement[741]	[]	655	"printf("No!\n")"
@Pattern	Unknown	0	0	#expr	Implicator[702]	[]	652	"is_primitive(x, y, z)"
@Pattern	Unknown	0	0	#cons	SaveAssignStatement[704]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#stat	Implicator[703]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#expr	Implicator[719]	[]	652	"is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#cons	SaveAssignStatement[721]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#stat	Implicator[720]	[]	652	"is_primitive(x, y, z) && is_hereonal(x, y, z)"
@Pattern	Unknown	0	0	#trap	InitAssignStatement[634]	[]	646	"x"
@Pattern	Unknown	0	0	#cons	InitAssignStatement[634]	[]	646	"x"
@Pattern	Unknown	0	0	#trap	IfStatement[724]	[]	652	"if(is_primitive(x, y, z) && is_hereonal(x, y, z))             printf("Yes!\n");         else          printf("No!\n");"
@Pattern	Unknown	0	0	#trap	CallStatement[730]	[]	653	"printf("Yes!\n")"
@Pattern	Unknown	0	0	#trap	CallStatement[741]	[]	655	"printf("No!\n")"