@.QuickSort_vtable = global [0 x i8*] []
@.QS_vtable = global [4 x i8*] [i8* bitcast (i32 (i8*,i32)* @QS.Start to i8*), i8* bitcast (i32 (i8*,i32,i32)* @QS.Sort to i8*), i8* bitcast (i32 (i8*)* @QS.Print to i8*), i8* bitcast (i32 (i8*,i32)* @QS.Init to i8*)]

declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
    %_str = bitcast [4 x i8]* @_cint to i8*
    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
    ret void
}

define void @throw_oob() {
    %_str = bitcast [15 x i8]* @_cOOB to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define i32 @main() {
%_0 = call i8* @calloc(i32 1, i32 20)
%_1 = bitcast i8* %_0 to i8***
%_2 = getelementptr [4 x i8*], [4 x i8*]* @.QS_vtable, i32 0, i32 0
store i8** %_2, i8*** %_1
%_3 = bitcast i8* %_0 to i8***
%_4 = load i8**, i8*** %_3
%_5 = getelementptr i8*, i8** %_4, i32 0
%_6 = load i8*, i8** %_5
%_7 = bitcast i8* %_6 to i32 (i8*, i32)*
%_8 = call i32 %_7(i8* %_0, i32 10)
call void (i32) @print_int(i32 %_8)

ret i32 0
}

define i32 @QS.Start(i8* %this, i32 %.sz) {
%sz = alloca i32
store i32 %.sz, i32* %sz
%aux01 = alloca i32

%_0 = bitcast i8* %this to i8***
%_1 = load i8**, i8*** %_0
%_2 = getelementptr i8*, i8** %_1, i32 3
%_3 = load i8*, i8** %_2
%_4 = bitcast i8* %_3 to i32 (i8*, i32)*
%_5 = load i32, i32* %sz
%_6 = call i32 %_4(i8* %this, i32 %_5)
store i32 %_6, i32* %aux01

%_7 = bitcast i8* %this to i8***
%_8 = load i8**, i8*** %_7
%_9 = getelementptr i8*, i8** %_8, i32 2
%_10 = load i8*, i8** %_9
%_11 = bitcast i8* %_10 to i32 (i8*)*
%_12 = call i32 %_11(i8* %this)
store i32 %_12, i32* %aux01
call void (i32) @print_int(i32 9999)

%_13 = getelementptr i8, i8* %this, i32 16
%_14 = bitcast i8* %_13 to i32*
%_15 = load i32, i32* %_14
%_16 = sub i32 %_15, 1
store i32 %_16, i32* %aux01

%_17 = bitcast i8* %this to i8***
%_18 = load i8**, i8*** %_17
%_19 = getelementptr i8*, i8** %_18, i32 1
%_20 = load i8*, i8** %_19
%_21 = bitcast i8* %_20 to i32 (i8*, i32, i32)*
%_22 = load i32, i32* %aux01
%_23 = call i32 %_21(i8* %this, i32 0, i32 %_22)
store i32 %_23, i32* %aux01

%_24 = bitcast i8* %this to i8***
%_25 = load i8**, i8*** %_24
%_26 = getelementptr i8*, i8** %_25, i32 2
%_27 = load i8*, i8** %_26
%_28 = bitcast i8* %_27 to i32 (i8*)*
%_29 = call i32 %_28(i8* %this)
store i32 %_29, i32* %aux01

ret i32 0
}

define i32 @QS.Sort(i8* %this, i32 %.left, i32 %.right) {
%left = alloca i32
store i32 %.left, i32* %left
%right = alloca i32
store i32 %.right, i32* %right
%v = alloca i32
%i = alloca i32
%j = alloca i32
%nt = alloca i32
%t = alloca i32
%cont01 = alloca i1
%cont02 = alloca i1
%aux03 = alloca i32

store i32 0, i32* %t
%_0 = load i32, i32* %left
%_1 = load i32, i32* %right
%_2 = icmp slt i32 %_0, %_1
br i1 %_2, label %if3, label %if4
if3:


%_6 = getelementptr i8, i8* %this, i32 8
%_7 = bitcast i8* %_6 to i32**
%_8 = load i32*, i32** %_7
%_9 = load i32, i32* %right
%_10 = load i32, i32* %_8
%_11 = icmp ult i32 %_9, %_10
br i1 %_11, label %oob12, label %oob13
oob12:
%_15 = add i32 %_9, 1
%_16 = getelementptr i32, i32* %_8, i32 %_15
%_17 = load i32, i32* %_16
br label %oob14
oob13:
call void @throw_oob()
br label %oob14
oob14:
store i32 %_17, i32* %v

%_18 = load i32, i32* %left
%_19 = sub i32 %_18, 1
store i32 %_19, i32* %i

%_20 = load i32, i32* %right
store i32 %_20, i32* %j

store i1 1, i1* %cont01
br label %loop21
loop21:
%_22 = load i1, i1* %cont01
br i1 %_22, label %loop23, label %loop24
loop23:

store i1 1, i1* %cont02
br label %loop25
loop25:
%_26 = load i1, i1* %cont02
br i1 %_26, label %loop27, label %loop28
loop27:

%_29 = load i32, i32* %i
%_30 = add i32 %_29, 1
store i32 %_30, i32* %i


%_31 = getelementptr i8, i8* %this, i32 8
%_32 = bitcast i8* %_31 to i32**
%_33 = load i32*, i32** %_32
%_34 = load i32, i32* %i
%_35 = load i32, i32* %_33
%_36 = icmp ult i32 %_34, %_35
br i1 %_36, label %oob37, label %oob38
oob37:
%_40 = add i32 %_34, 1
%_41 = getelementptr i32, i32* %_33, i32 %_40
%_42 = load i32, i32* %_41
br label %oob39
oob38:
call void @throw_oob()
br label %oob39
oob39:
store i32 %_42, i32* %aux03
%_43 = load i32, i32* %aux03
%_44 = load i32, i32* %v
%_45 = icmp slt i32 %_43, %_44
%_46 = xor i1 1, %_45
br i1 %_46, label %if47, label %if48
if47:

store i1 0, i1* %cont02
br label %if49
if48:

store i1 1, i1* %cont02
br label %if49
if49:
br label %loop25
loop28:

store i1 1, i1* %cont02
br label %loop50
loop50:
%_51 = load i1, i1* %cont02
br i1 %_51, label %loop52, label %loop53
loop52:

%_54 = load i32, i32* %j
%_55 = sub i32 %_54, 1
store i32 %_55, i32* %j


%_56 = getelementptr i8, i8* %this, i32 8
%_57 = bitcast i8* %_56 to i32**
%_58 = load i32*, i32** %_57
%_59 = load i32, i32* %j
%_60 = load i32, i32* %_58
%_61 = icmp ult i32 %_59, %_60
br i1 %_61, label %oob62, label %oob63
oob62:
%_65 = add i32 %_59, 1
%_66 = getelementptr i32, i32* %_58, i32 %_65
%_67 = load i32, i32* %_66
br label %oob64
oob63:
call void @throw_oob()
br label %oob64
oob64:
store i32 %_67, i32* %aux03
%_68 = load i32, i32* %v
%_69 = load i32, i32* %aux03
%_70 = icmp slt i32 %_68, %_69
%_71 = xor i1 1, %_70
br i1 %_71, label %if72, label %if73
if72:

store i1 0, i1* %cont02
br label %if74
if73:

store i1 1, i1* %cont02
br label %if74
if74:
br label %loop50
loop53:


%_75 = getelementptr i8, i8* %this, i32 8
%_76 = bitcast i8* %_75 to i32**
%_77 = load i32*, i32** %_76
%_78 = load i32, i32* %i
%_79 = load i32, i32* %_77
%_80 = icmp ult i32 %_78, %_79
br i1 %_80, label %oob81, label %oob82
oob81:
%_84 = add i32 %_78, 1
%_85 = getelementptr i32, i32* %_77, i32 %_84
%_86 = load i32, i32* %_85
br label %oob83
oob82:
call void @throw_oob()
br label %oob83
oob83:
store i32 %_86, i32* %t

%_87 = getelementptr i8, i8* %this, i32 8
%_88 = bitcast i8* %_87 to i32**
%_89 = load i32*, i32** %_88
%_90 = load i32, i32* %i
%_91 = load i32, i32* %_89
%_92 = icmp ult i32 %_90, %_91
br i1 %_92, label %oob93, label %oob94
oob93:
%_96 = add i32 %_90, 1
%_97 = getelementptr i32, i32* %_89, i32 %_96

%_98 = getelementptr i8, i8* %this, i32 8
%_99 = bitcast i8* %_98 to i32**
%_100 = load i32*, i32** %_99
%_101 = load i32, i32* %j
%_102 = load i32, i32* %_100
%_103 = icmp ult i32 %_101, %_102
br i1 %_103, label %oob104, label %oob105
oob104:
%_107 = add i32 %_101, 1
%_108 = getelementptr i32, i32* %_100, i32 %_107
%_109 = load i32, i32* %_108
br label %oob106
oob105:
call void @throw_oob()
br label %oob106
oob106:
store i32 %_109, i32* %_97
br label %oob95
oob94:
call void @throw_oob()
br label %oob95
oob95:

%_110 = getelementptr i8, i8* %this, i32 8
%_111 = bitcast i8* %_110 to i32**
%_112 = load i32*, i32** %_111
%_113 = load i32, i32* %j
%_114 = load i32, i32* %_112
%_115 = icmp ult i32 %_113, %_114
br i1 %_115, label %oob116, label %oob117
oob116:
%_119 = add i32 %_113, 1
%_120 = getelementptr i32, i32* %_112, i32 %_119
%_121 = load i32, i32* %t
store i32 %_121, i32* %_120
br label %oob118
oob117:
call void @throw_oob()
br label %oob118
oob118:
%_122 = load i32, i32* %j
%_123 = load i32, i32* %i
%_124 = add i32 %_123, 1
%_125 = icmp slt i32 %_122, %_124
br i1 %_125, label %if126, label %if127
if126:

store i1 0, i1* %cont01
br label %if128
if127:

store i1 1, i1* %cont01
br label %if128
if128:
br label %loop21
loop24:

%_129 = getelementptr i8, i8* %this, i32 8
%_130 = bitcast i8* %_129 to i32**
%_131 = load i32*, i32** %_130
%_132 = load i32, i32* %j
%_133 = load i32, i32* %_131
%_134 = icmp ult i32 %_132, %_133
br i1 %_134, label %oob135, label %oob136
oob135:
%_138 = add i32 %_132, 1
%_139 = getelementptr i32, i32* %_131, i32 %_138

%_140 = getelementptr i8, i8* %this, i32 8
%_141 = bitcast i8* %_140 to i32**
%_142 = load i32*, i32** %_141
%_143 = load i32, i32* %i
%_144 = load i32, i32* %_142
%_145 = icmp ult i32 %_143, %_144
br i1 %_145, label %oob146, label %oob147
oob146:
%_149 = add i32 %_143, 1
%_150 = getelementptr i32, i32* %_142, i32 %_149
%_151 = load i32, i32* %_150
br label %oob148
oob147:
call void @throw_oob()
br label %oob148
oob148:
store i32 %_151, i32* %_139
br label %oob137
oob136:
call void @throw_oob()
br label %oob137
oob137:

%_152 = getelementptr i8, i8* %this, i32 8
%_153 = bitcast i8* %_152 to i32**
%_154 = load i32*, i32** %_153
%_155 = load i32, i32* %i
%_156 = load i32, i32* %_154
%_157 = icmp ult i32 %_155, %_156
br i1 %_157, label %oob158, label %oob159
oob158:
%_161 = add i32 %_155, 1
%_162 = getelementptr i32, i32* %_154, i32 %_161

%_163 = getelementptr i8, i8* %this, i32 8
%_164 = bitcast i8* %_163 to i32**
%_165 = load i32*, i32** %_164
%_166 = load i32, i32* %right
%_167 = load i32, i32* %_165
%_168 = icmp ult i32 %_166, %_167
br i1 %_168, label %oob169, label %oob170
oob169:
%_172 = add i32 %_166, 1
%_173 = getelementptr i32, i32* %_165, i32 %_172
%_174 = load i32, i32* %_173
br label %oob171
oob170:
call void @throw_oob()
br label %oob171
oob171:
store i32 %_174, i32* %_162
br label %oob160
oob159:
call void @throw_oob()
br label %oob160
oob160:

%_175 = getelementptr i8, i8* %this, i32 8
%_176 = bitcast i8* %_175 to i32**
%_177 = load i32*, i32** %_176
%_178 = load i32, i32* %right
%_179 = load i32, i32* %_177
%_180 = icmp ult i32 %_178, %_179
br i1 %_180, label %oob181, label %oob182
oob181:
%_184 = add i32 %_178, 1
%_185 = getelementptr i32, i32* %_177, i32 %_184
%_186 = load i32, i32* %t
store i32 %_186, i32* %_185
br label %oob183
oob182:
call void @throw_oob()
br label %oob183
oob183:

%_187 = bitcast i8* %this to i8***
%_188 = load i8**, i8*** %_187
%_189 = getelementptr i8*, i8** %_188, i32 1
%_190 = load i8*, i8** %_189
%_191 = bitcast i8* %_190 to i32 (i8*, i32, i32)*
%_192 = load i32, i32* %left
%_193 = load i32, i32* %i
%_194 = sub i32 %_193, 1
%_195 = call i32 %_191(i8* %this, i32 %_192, i32 %_194)
store i32 %_195, i32* %nt

%_196 = bitcast i8* %this to i8***
%_197 = load i8**, i8*** %_196
%_198 = getelementptr i8*, i8** %_197, i32 1
%_199 = load i8*, i8** %_198
%_200 = bitcast i8* %_199 to i32 (i8*, i32, i32)*
%_201 = load i32, i32* %i
%_202 = add i32 %_201, 1
%_203 = load i32, i32* %right
%_204 = call i32 %_200(i8* %this, i32 %_202, i32 %_203)
store i32 %_204, i32* %nt
br label %if5
if4:

store i32 0, i32* %nt
br label %if5
if5:

ret i32 0
}

define i32 @QS.Print(i8* %this) {
%j = alloca i32

store i32 0, i32* %j
br label %loop0
loop0:
%_1 = load i32, i32* %j
%_2 = getelementptr i8, i8* %this, i32 16
%_3 = bitcast i8* %_2 to i32*
%_4 = load i32, i32* %_3
%_5 = icmp slt i32 %_1, %_4
br i1 %_5, label %loop6, label %loop7
loop6:

%_8 = getelementptr i8, i8* %this, i32 8
%_9 = bitcast i8* %_8 to i32**
%_10 = load i32*, i32** %_9
%_11 = load i32, i32* %j
%_12 = load i32, i32* %_10
%_13 = icmp ult i32 %_11, %_12
br i1 %_13, label %oob14, label %oob15
oob14:
%_17 = add i32 %_11, 1
%_18 = getelementptr i32, i32* %_10, i32 %_17
%_19 = load i32, i32* %_18
br label %oob16
oob15:
call void @throw_oob()
br label %oob16
oob16:
call void (i32) @print_int(i32 %_19)

%_20 = load i32, i32* %j
%_21 = add i32 %_20, 1
store i32 %_21, i32* %j
br label %loop0
loop7:

ret i32 0
}

define i32 @QS.Init(i8* %this, i32 %.sz) {
%sz = alloca i32
store i32 %.sz, i32* %sz

%_0 = load i32, i32* %sz
%_1 = getelementptr i8, i8* %this, i32 16
%_2 = bitcast i8* %_1 to i32*
store i32 %_0, i32* %_2

%_3 = load i32, i32* %sz
%_4 = icmp slt i32 %_3, 0
br i1 %_4, label %arr_alloc5, label %arr_alloc6
arr_alloc5:
call void @throw_oob()
br label %arr_alloc6
arr_alloc6:
%_7 = add i32 %_3, 1
%_8 = call i8* @calloc(i32 4, i32 %_7)
%_9 = bitcast i8* %_8 to i32*
store i32 %_3, i32* %_9
%_10 = getelementptr i8, i8* %this, i32 8
%_11 = bitcast i8* %_10 to i32**
store i32* %_9, i32** %_11

%_12 = getelementptr i8, i8* %this, i32 8
%_13 = bitcast i8* %_12 to i32**
%_14 = load i32*, i32** %_13
%_15 = load i32, i32* %_14
%_16 = icmp ult i32 0, %_15
br i1 %_16, label %oob17, label %oob18
oob17:
%_20 = add i32 0, 1
%_21 = getelementptr i32, i32* %_14, i32 %_20
store i32 20, i32* %_21
br label %oob19
oob18:
call void @throw_oob()
br label %oob19
oob19:

%_22 = getelementptr i8, i8* %this, i32 8
%_23 = bitcast i8* %_22 to i32**
%_24 = load i32*, i32** %_23
%_25 = load i32, i32* %_24
%_26 = icmp ult i32 1, %_25
br i1 %_26, label %oob27, label %oob28
oob27:
%_30 = add i32 1, 1
%_31 = getelementptr i32, i32* %_24, i32 %_30
store i32 7, i32* %_31
br label %oob29
oob28:
call void @throw_oob()
br label %oob29
oob29:

%_32 = getelementptr i8, i8* %this, i32 8
%_33 = bitcast i8* %_32 to i32**
%_34 = load i32*, i32** %_33
%_35 = load i32, i32* %_34
%_36 = icmp ult i32 2, %_35
br i1 %_36, label %oob37, label %oob38
oob37:
%_40 = add i32 2, 1
%_41 = getelementptr i32, i32* %_34, i32 %_40
store i32 12, i32* %_41
br label %oob39
oob38:
call void @throw_oob()
br label %oob39
oob39:

%_42 = getelementptr i8, i8* %this, i32 8
%_43 = bitcast i8* %_42 to i32**
%_44 = load i32*, i32** %_43
%_45 = load i32, i32* %_44
%_46 = icmp ult i32 3, %_45
br i1 %_46, label %oob47, label %oob48
oob47:
%_50 = add i32 3, 1
%_51 = getelementptr i32, i32* %_44, i32 %_50
store i32 18, i32* %_51
br label %oob49
oob48:
call void @throw_oob()
br label %oob49
oob49:

%_52 = getelementptr i8, i8* %this, i32 8
%_53 = bitcast i8* %_52 to i32**
%_54 = load i32*, i32** %_53
%_55 = load i32, i32* %_54
%_56 = icmp ult i32 4, %_55
br i1 %_56, label %oob57, label %oob58
oob57:
%_60 = add i32 4, 1
%_61 = getelementptr i32, i32* %_54, i32 %_60
store i32 2, i32* %_61
br label %oob59
oob58:
call void @throw_oob()
br label %oob59
oob59:

%_62 = getelementptr i8, i8* %this, i32 8
%_63 = bitcast i8* %_62 to i32**
%_64 = load i32*, i32** %_63
%_65 = load i32, i32* %_64
%_66 = icmp ult i32 5, %_65
br i1 %_66, label %oob67, label %oob68
oob67:
%_70 = add i32 5, 1
%_71 = getelementptr i32, i32* %_64, i32 %_70
store i32 11, i32* %_71
br label %oob69
oob68:
call void @throw_oob()
br label %oob69
oob69:

%_72 = getelementptr i8, i8* %this, i32 8
%_73 = bitcast i8* %_72 to i32**
%_74 = load i32*, i32** %_73
%_75 = load i32, i32* %_74
%_76 = icmp ult i32 6, %_75
br i1 %_76, label %oob77, label %oob78
oob77:
%_80 = add i32 6, 1
%_81 = getelementptr i32, i32* %_74, i32 %_80
store i32 6, i32* %_81
br label %oob79
oob78:
call void @throw_oob()
br label %oob79
oob79:

%_82 = getelementptr i8, i8* %this, i32 8
%_83 = bitcast i8* %_82 to i32**
%_84 = load i32*, i32** %_83
%_85 = load i32, i32* %_84
%_86 = icmp ult i32 7, %_85
br i1 %_86, label %oob87, label %oob88
oob87:
%_90 = add i32 7, 1
%_91 = getelementptr i32, i32* %_84, i32 %_90
store i32 9, i32* %_91
br label %oob89
oob88:
call void @throw_oob()
br label %oob89
oob89:

%_92 = getelementptr i8, i8* %this, i32 8
%_93 = bitcast i8* %_92 to i32**
%_94 = load i32*, i32** %_93
%_95 = load i32, i32* %_94
%_96 = icmp ult i32 8, %_95
br i1 %_96, label %oob97, label %oob98
oob97:
%_100 = add i32 8, 1
%_101 = getelementptr i32, i32* %_94, i32 %_100
store i32 19, i32* %_101
br label %oob99
oob98:
call void @throw_oob()
br label %oob99
oob99:

%_102 = getelementptr i8, i8* %this, i32 8
%_103 = bitcast i8* %_102 to i32**
%_104 = load i32*, i32** %_103
%_105 = load i32, i32* %_104
%_106 = icmp ult i32 9, %_105
br i1 %_106, label %oob107, label %oob108
oob107:
%_110 = add i32 9, 1
%_111 = getelementptr i32, i32* %_104, i32 %_110
store i32 5, i32* %_111
br label %oob109
oob108:
call void @throw_oob()
br label %oob109
oob109:

ret i32 0
}
