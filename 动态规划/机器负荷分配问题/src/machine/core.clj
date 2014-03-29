(ns machine.core)
(use 'numeric.expresso.core)

;; defmulti
;; Creates a new multimethod with the associated dispatch function.

;; 定义基本常量
(def 高负荷机器完好率 0.7)
(def 低负荷机器完好率 0.9)
(def 高负荷单台年产量 8)
(def 低负荷单台年产量 5)
(def 机器数量 1000)

;; 决策允许集合中可取的决策数量很大,
;; 一一列举计算量很大,
;; 不妨认为状态变量和决策变量都是连续的,得到最优解后,再取整

;; 第五年产量
(def f5
  (optimize
   (ex (+ (* ~高负荷单台年产量 d5)
          (* ~低负荷单台年产量 (- x5 d5))))))
