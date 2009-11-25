(<= 1 1 2 3 5 8 13) ; => T

(<= 1 2 3 4) ; => T

(<= 1 2 3 2) ; => NIL

(<= 1 "Hi") ; => TYPE-ERROR

(<= 1 2 3 4) ; => T

(<=) ; error, not enough args

(< 1 2 3 4) ; => T

(< 1 1 2 3 5) ; => NIL

(< 1 3) ; => T

(< 2 1) ; => NIL

(< 1 "hi") ; type error

(<) ; not enough args

(= 1 1) ; => T

(= 1.0 -1.0) ; => NIL

(= 0.0 -0.0) ; => T

(= 1 2) ; => NIL

(= 7 7 7) ; => T
