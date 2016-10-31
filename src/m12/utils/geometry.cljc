(ns m12.utils.geometry)

(defn cos [x]
  #?(:cljs (.cos js/Math x)
     :clj (Math/cos x)))

(defn sin [x]
  #?(:cljs (.sin js/Math x)
     :clj (Math/sin x)))

(def PI
  #?(:cljs js/Math.PI :clj Math/PI))

(defn v+
  "2D-vector sum"
  ([v] v)
  ([[x1 y1] [x2 y2]]
   [(+ x1 x2) (+ y1 y2)])
  ([[x1 y1] [x2 y2] [x3 y3]]
   [(+ x1 x2 x3) (+ y1 y2 y3)])
  ([v1 v2 v3 & more]
   (reduce v+ (v+ v1 v2 v3) more)))

(defn v*
  "2D vector scaling"
  [scalar [x y]]
  [(* scalar x) (* scalar y)])

(defn v-
  [v1 v2]
  (v+ v1 (v* -1 v2)))

(defn u>
  [theta]
  [(cos theta) (sin theta)])

(defn v>
  [theta]
  [(- (sin theta)) (cos theta)])

(defn vx [[x y]]
  x)

(defn vy [[x y]]
  y)

(defn vdet
  "determinant of 2 vectors"
  [u v]
  (-
    (* (vx u) (vy v))
    (* (vx v) (vy u))))

(defn intersection-point
  "Given points A and B, each of which has support vector uA and uB,
  computes the intersection points of the line corssing each point with
  each support vector"
  [A uA B uB]
  (v+ A (v* (/
              (vdet (v- B A) uB)
              (vdet uA uB))
          uA)))


