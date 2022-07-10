using import String

let C = (import C.string)
let stbsp = (import stb.sprintf)

let start-token end-token = "${" "}"

""""Get the inner code inside a template.
    @param start rawstring @pointer to start of template, at start-token
    @param end rawstring @pointer to end of template, at end-token
    @return (:_ rawstring usize)
fn get-inner (start end)
    let start-offset end-offset = (ptrtoint start usize) (ptrtoint end usize)
    _
        inttoptr (start-offset + (countof start-token)) rawstring
        end-offset - start-offset - (countof start-token)

inline ptr-offset (start end)
    (ptrtoint end usize) - (ptrtoint start usize)

""""Parse a templated string into a list of interleaved code strings.
    @param input (array i8) @constant string literal containing the templated string
    @return List
fn parse-template (input)
    # (prefix .. code .. suffix)+ | input
    loop (next result = (input as rawstring) (list))
        let start = (C.strstr next start-token)
        if (start != null)
            let end = (C.strstr start end-token)
            if (end != null)
                # add anything between current and previous
                let result =
                    if (next != start)
                        let prefix = (string next (ptr-offset next start))
                        cons prefix result
                    else
                        result

                let inner len = (get-inner start end)
                repeat
                    as
                        & (end @ (countof end-token))
                        rawstring
                    cons (string inner len) result

        # no more templates
        let next-idx = (ptr-offset (input as rawstring) next)
        if (next-idx == (countof input))
            break ('reverse result)
        else
            let remainder = (string next ((countof input) - next-idx))
            break ('reverse (cons remainder result))

inline prefix:f (str)
    print (parse-template str)
    String str

do
    let prefix:f
    locals;
