;;; - TEST NUMERIC COMPARISONS

(defvar one 1)

(defvar two 2)

(<= 1 1 2 3 5 8 13) ; => T

(<= 1 2 3 4) ; => T

(<= one one two) ; => T

(<= one two ((lambda (x) (+ x 2)) one)) ; => T

(<= 1 2 3 2) ; => NIL

(<= 1 "Hi") ; => TYPE-ERROR

(<= 1 2 3 4) ; => T

(<=) ; error, not enough args

(< 1 2 3 4) ; => T

(< 1 1 2 3 5) ; => NIL

(< 1 3) ; => T

(< one two) ; => T

(< one two ((lambda (x) (+ x 2)) one)) ; => T

(< 2 1) ; => NIL

(< 1 "hi") ; type error

(<) ; not enough args

(= 1 1) ; => T

(= one one) ; => T

(= two ((lambda (x) (+ x 2)) 0)) ; => T

(= 1.0 -1.0) ; => NIL

(= 0.0 -0.0) ; => T

(= 1 2) ; => NIL

(= 7 7 7) ; => T

(> 3 2 1) ; => T

(> 14 10) ; => T

(> two one) ; => T

(> two one ((lambda (x) (- x 2)) one)) ; => T

(> 10 14) ; => NIL

(> 1 2 3) ; => NIL

(> 8 7 6 4 5 3 2) ; => NIL

(> 5 "hi") ; type error

(>) ; not enough args

(>= 7 7 7) ; => T

(>= 8 6 4 2 2 1) ; => T

(>= 2 1) ; => T

(>= two two one one) ; => T

(>= two ((lambda (x) (+ x 1)) one) one) ; => T

(>= 8 6 4 2 3 1) ; => NIL

(>= 3 5) ; => NIL

(>= 3 "hi") ; type error

(>=) ; not enough args

;;; - TEST NUMERIC OPERATIONS

(+) ; => 0

(+ 5) ; => 5

(+ 1 5) ; => 6

(+ 30.05 0.7 1.25) ; => 32

(+ one two) ; => 3

(+ 5 "hi") ; => error: type

(-) ; => error: arg #

(- 5) ; => -5

(- 7 2) ; => 5

(- 2 7) ; => -5

(- 25 3 4 5 6 7) ; => 0

(- 100 75.4 24.6) ; => 0

(- two one one one) ; => -1

(*) ; => 1

(* 5) ; => 5

(* 1 5) ; => 5

(* 5 5) ; => 25

(* two two) ; => 4

(* 2 3 4) ; => 24

(* -23 43) ; => -989

(* 5 "hi") ; => error: type

(/) ; => error: arg #

(/ 5) ; => 0.2

(/ 10 2) ; => 5

(/ two one) ; => 2

(/ 100 5 2) ; => 10

;; (/ 33 3) ; => 11

;;; - TEST LAMBDAs

(defvar sq (lambda (x) (* x x))) ; => SQ

sq ; => <LAMBDA (X) >

(funcall sq 5)  ; => 25

#'sq ; => undefined function SQ

(sq 5) ; => undefined function SQ

((lambda (x) (funcall sq x)) 6) ; => 36

;; - test persistent values of lexical closure
(let* ((x 2) (sqx (lambda () (setq x (* x x))))) 
    (funcall sqx)   ; local x = 4 after this call
    (defparameter *sqx* sqx))

(funcall *sqx*) ; => 16

;; - check binding (the lexical closure overrides the calling context)
(let ((x 1)) (funcall *sqx*)) ; warning, x defined but unsed => 256

;; - check binding (the lexical closure includes non-local variables)
(let ((x 1))
    (let ((y 2)) (defparameter *sqy* (lambda () (setq x (* x x))))))

(funcall *sqy*) ; => 1

(let ((x 2)) (funcall *sqy*)) ; => 1

;; - check binding (the lexical closue does NOT include unreferenced variables)
(let ((a 50) (b 60) (c 7))
    (defparameter *sqz* (lambda (fun2arg) (+ c (funcall fun2arg)))))  ; => *SQZ*

(let ((a 0) (b 0) (c 0))
    (funcall *sqz* (lambda () (+ a b))))    ; => 7

;; - check binding (the lexical closure includes function definitions)
(defparameter funlam 
    (let ((x 5))
        (defun f1 (i) (+ i 1))
        (lambda () (f1 x))))    ; => FUNLAM


(funcall funlam)    ; => 6

;;; SAMPLE ALGORITHMS

(defun fibonacci (n) (if (<= n 1) 
    1 
    (+ 
        (fibonacci (- n 1)) 
        (fibonacci (- n 2)))))

(fibonacci 0) ; => 1

(fibonacci 1) ; => 1

(fibonacci 2) ; => 2

(fibonacci 3) ; => 3

(fibonacci 4) ; => 5

(fibonacci 5) ; => 8

(fibonacci 6) ; => 13

(fibonacci 20) ; => 10946

(defun collatz (n) (if (= n 1)
    1
    (if (= 0 (mod n 2))
        (+ 1 (collatz (/ n 2)))
        (+ 1 (collatz (+ (* n 3) 1))))))

;;; LABELS/LETREC TESTS

;; LABELS is similar to LETREC from Scheme, but some key differences remain.
;; In Scheme, if you store a lambda to a variable, you can treat that variable
;; as a function. In Common Lisp you cannot, you must explicitly use the FUNCALL
;; form to invoke the function. For example, in Scheme you can do this:
;;
;;  (let ((plus5 (lambda (x) (+ x 5)))) (plus5 3)) => 8
;;
;; In Common Lisp, this will result in an undefined function error. The syntax
;; for the same thing in CL is:
;;
;;  (let ((plus5 (lambda (x) (+ x 5)))) (funcall plus5 3)) => 8
;;
;; More specifically to LABELS vs. LETREC: Since CL allows functions and
;; variables to share names, the functions of CL generally operate on one or
;; the other, but not both. LET creates locally-scoped variables. LABELS
;; creates locally-scoped functions. So, to rewriting the LETREC example from
;; notes15 using LABELS looks like:
(labels
    ((factorial (n) 
        (if (<= n 1)
            1
            (* n (factorial (- n 1))))))
    (factorial 10)) ; => 3628800

;; rewriting the LETREC example using LET looks like:
(let
    ((factorial
        (lambda (n)
            (if (= n 0)
                1
                (* n (funcall factorial (- n 1)))))))
    (funcall factorial 5))

;; final note regarding LETREC and LET. In a compliant Common Lisp
;; implementation, LET does not allow recursive definition similar to
;; LETREC. As I noted in my presentation, my implementation of LET does
;; allow recursive definition.
