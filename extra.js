const c = {
    a(e, t, n) {
        return s(e, t - 1, n)
    },
    b(e, t, n) {
        return s(e, t - 2, n)
    },
    c(e, t, n) {
        return s(e, t - 3, n)
    },
    d(e, t, n) {
        return s(e, t - 4, n)
    },
    e(e, t, n) {
        return s(e, t - 5, n)
    },
    f(e, t, n) {
        return s(e, t, n)
    },
    g(e, t, n) {
        return s(e, t + 1, n)
    },
    h(e, t, n) {
        return s(e, t + 2, n)
    },
    i(e, t, n) {
        return s(e, t + 3, n)
    },
    j(e, t, n) {
        return s(e, t + 4, n)
    },
    k(e, t, n) {
        return s(e, t + 5, n)
    },
    l(e, t, n) {
        return s(`/${e}/`, t - 1, n)
    },
    m(e, t, n) {
        return s(`/${e}/`, t - 2, n)
    },
    w() {
        return new Date
    },
    n(e, t, n) {
        return s(`/${e}/`, t - 3, n)
    },
    o(e, t, n) {
        return s(`/${e}/`, t - 4, n)
    },
    p(e, t, n) {
        return s(`/${e}/`, t - 5, n)
    },
    q(e, t, n) {
        return s(`/${e}/`, t, n)
    },
    r(e, t, n) {
        return s(`/${e}/`, t + 1, n)
    },
    s(e, t, n) {
        return s(`/${e}/`, t + 2, n)
    },
    t(e, t, n) {
        return s(`/${e}/`, t + 3, n)
    },
    u(e, t, n) {
        return s(`/${e}/`, t + 4, n)
    },
    v(e, t, n) {
        return s(`/${e}/`, t + 5, n)
    }
}
var md5Func = Java.type('cn.pprocket.utils.SignService');
var System = Java.type('java.lang.System');
function md5(content) {
    System.out.println(Object.keys(md5Func.Companion));
    return md5Func.Companion.md5(content);
}
function r(e) {
    return e.reduce((function(e, t, n) {
            return e + t
        }
    ))
}
function l(e) {
    const i = ["a", "b", "e", "g", "h", "i", "m", "n", "o", "p", "q", "r", "s", "t", "u", "w"];
    function a(e) {
        return 128 & e ? 255 & (e << 1 ^ 27) : e << 1
    }
    function o(e) {
        return a(e) ^ e
    }
    function s(e) {
        return o(a(e))
    }
    function r(e) {
        return s(o(a(e)))
    }
    function c(e) {
        return r(e) ^ s(e) ^ o(e)
    }
    let t = [0, 0, 0, 0];
    return t[0] = c(e[0]) ^ r(e[1]) ^ s(e[2]) ^ o(e[3]),
        t[1] = o(e[0]) ^ c(e[1]) ^ r(e[2]) ^ s(e[3]),
        t[2] = s(e[0]) ^ o(e[1]) ^ c(e[2]) ^ r(e[3]),
        t[3] = r(e[0]) ^ s(e[1]) ^ o(e[2]) ^ c(e[3]),
        e[0] = t[0],
        e[1] = t[1],
        e[2] = t[2],
        e[3] = t[3],
        e
}
const i = {
    xx : l
}
function hkey(e, t, n) {
    t++;
    e = `/${e.split("/").filter(Boolean).join("/")}/`;

    const s = "JKMNPQRTX1234OABCDFG56789H";

    const c = md5((n + s).replace(/[^0-9]/g, ""));
    const l = md5(t + e + c);

    let m = l.replace(/[^0-9]/g, "").slice(0, 9)
    console.log(typeof m)
    m.padEnd(9, '0');
    let _ = Number(m);
    let a = "";
    for (let i = 0; i < 5; i++) {
        let t = _ % s.length;
        _ = Math.floor(_ / s.length);
        a += s[t];
    }
    const charCodes = a.slice(-4).split("").map(e => e.charCodeAt());
    let d = ("" + r(i.xx(charCodes)) % 100).padStart(2, '0');

    // 返回最终结果
    return a + d;
}
