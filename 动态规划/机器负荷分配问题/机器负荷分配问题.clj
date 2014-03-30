;; defmulti
;; Creates a new multimethod with the associated dispatch function.

;; 定义基本常量
(def 高负荷机器完好率 0.7)
(def 低负荷机器完好率 0.9)
(def 高负荷单台年产量 8)
(def 低负荷单台年产量 5)
(def 机器数量 1000)
(def 年数 5)

;; 决策允许集合中可取的决策数量很大,
;; 一一列举计算量很大,
;; 不妨认为状态变量和决策变量都是连续的,得到最优解后,再取整

;; 定义模型
;; 阶段k: 运行年份 k = 1, 2, 3, 4, 5, 6 (k=1 => 第一年初)
;; 状态变量xk: 第k年初完好的机器数 k = 1, 2, 3, 4, 5, 6 (x6 第六年初的完好机器数)
;; 决策变量dk: 第k年投入高负荷运行的机器数
;; 状态转移方程: x(k+1) = 0.7dk + 0.9(xk - dk)
;; 决策允许集合: Dk(xk) = {dk | 0 <= dk <= xk}
;; 阶段指标: vk(xk, dk) = 8dk + 5(xk - dk)
;; 终端条件: f6(x6) = 0

;; fk(xk) = max{vk(xk, dk) + f(k+1)(x(k+1))}
;;        = max{8dk + 5(xk - dk) + f(k+1)[0.7dk + 0.9(xk - dk)]}

;; f5(x5) = max{ 8d5 + 5(x5 - d5) }
;;        = max{ 3d5 + 5 * x5 }
;; 显然 d5 越多越好，则有
;; f5(x5) = 8x5

;; f4(x4) = max{ 3d4 + 5x4 + f5(x5) }
;;        = max{ 3d4 + 5x4 + 8x5 }
;;        = max{ 3d4 + 5x4 + 8(0.7d4 + 0.9(x4 - d4)) }
;;        = max{ (3 - 8 * 0.2)d4 + (5 + 8 * 0.9)x4 }
;;        = max{ 1.4d4 + 12.2x4 }

;; 显然任何一个 f(n) 可以表示为由 max {wn1 * dn + wn2 * xn} 的表达式
;; 且最终化简后可以表示为 wn * xn

;; (def w5 [3 5])
;; ;; 3 > 0 所以 w5 取 3 + 5 = 8
;; (def w5-final
;;   (if (> (first w5) 0)
;;     (apply + w5)
;;     (last w5)))

;; (def w4 [(+ 3 (* w5-final -0.2)) (+ 5 (* w5-final 0.9))])


(defn calc-weight
  "w_d 是 dn 的系数，w_x 是 xn 的系数，last-weight，比如对第五年则为第6年"
  [last-weight]
  (let [w [(+ (- 高负荷单台年产量
                 低负荷单台年产量)
              (* last-weight (- 高负荷机器完好率 低负荷机器完好率)))
           (+ 低负荷单台年产量 (* last-weight 低负荷机器完好率))]]
    (if (> (first w) 0)
      (apply + w)
      (last w))))

(defn get-weight [n]
  (nth (iterate calc-weight 0) n))

(* (get-weight 年数) 机器数量)
