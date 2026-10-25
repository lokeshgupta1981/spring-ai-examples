import statistics as st, sys, collections
rows=[l.rstrip('\n').split('|',9) for l in open(sys.argv[1]) if l.startswith('run')]
by=collections.defaultdict(list)
for r in rows:
    run,_,mode,tid,exp,dep,calls,tok,ms,reply=r
    ok_dep = dep==exp
    rep=reply.replace('\\n','\n').strip()
    rules = rep.startswith(tid) and rep.rstrip().endswith('OrderDesk Support')
    by[(mode,run)].append((int(calls),int(tok),int(ms),ok_dep,rules,rep.startswith('ERROR')))
for mode in ['single','multi']:
    runs=sorted(k[1] for k in by if k[0]==mode)
    per=[]
    for run in runs:
        v=by[(mode,run)]
        per.append(dict(calls=sum(x[0] for x in v)/len(v),tok=st.median([x[1] for x in v]),toksum=sum(x[1] for x in v),
            sec=st.median([x[2] for x in v])/1000,dep=sum(x[3] for x in v),rules=sum(x[4] for x in v),err=sum(x[5] for x in v),n=len(v)))
        print(mode,run,per[-1])
    print(mode,'MEDIAN of runs', {k:st.median([p[k] for p in per]) for k in per[0]})
